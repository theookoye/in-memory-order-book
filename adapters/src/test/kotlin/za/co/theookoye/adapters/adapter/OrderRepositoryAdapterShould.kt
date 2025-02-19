package za.co.theookoye.adapters.adapter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.adapters.mother.GetRecentOrdersQueryMother
import za.co.theookoye.adapters.mother.OrderMother
import za.co.theookoye.adapters.mother.TestData
import za.co.theookoye.domain.exception.ConcurrentUpdateException
import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.OrderStatus
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class OrderRepositoryAdapterShould {
    
    private lateinit var repository: OrderRepositoryAdapter
    private val expectedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    private val clock = Clock.fixed(expectedDate, ZoneId.systemDefault())
    
    @BeforeEach
    fun setup() {
        repository = OrderRepositoryAdapter(clock)
    }
    
    @Test
    fun `save new order successfully`() {
        // given
        val givenOrder = OrderMother.of(id = "order1", version = 0)
        // when
        repository.save(givenOrder)
        // then
        repository.findById("order1") shouldBe givenOrder
    }
    
    @Test
    fun `increment version when updating existing order with matching version`() {
        // given
        val givenOrder = OrderMother.of(id = "order1", version = 1)
        repository.save(givenOrder)
        // when
        val updatedOrder = givenOrder.copy(price = givenOrder.price.plus(BigDecimal.TEN))
        repository.save(updatedOrder)
        // then
        repository.findById("order1")?.version shouldBe 2
    }
    
    @Test
    fun `throw ConcurrentUpdateException when versions don't match`() {
        // given
        val givenOrder = OrderMother.of(id = "order1", version = 1)
        repository.save(givenOrder)
        // when - then
        shouldThrow<ConcurrentUpdateException> {
            repository.save(givenOrder.copy(version = 2))
        }
    }
    
    @Test
    fun `find orders by currency pair and side`() {
        // given
        val givenBtcBuyOrder = OrderMother.of(
            currencyPair = "BTC" + TestData.currencyCode(),
            side = OrderSide.BUY
        )
        val givenBtcSellOrder = OrderMother.of(
            currencyPair = givenBtcBuyOrder.currencyPair,
            side = OrderSide.SELL
        )
        repository.save(givenBtcBuyOrder)
        repository.save(givenBtcSellOrder)
        val givenQuery = GetRecentOrdersQueryMother.of(
            limit = 100,
            currencyPairs = setOf(givenBtcBuyOrder.currencyPair),
            sides = setOf(OrderSide.BUY)
        )
        // when
        val result = repository.find(givenQuery)
        // then
        result shouldContainExactly listOf(givenBtcBuyOrder)
    }
    
    @Test
    fun `find orders within date range`() {
        // given
        val givenPastOrder = OrderMother.of(updatedAt = expectedDate.minus(1, ChronoUnit.DAYS))
        val givenOrder = OrderMother.of(updatedAt = expectedDate)
        repository.save(givenPastOrder)
        repository.save(givenOrder)
        val givenQuery = GetRecentOrdersQueryMother.of(
            rangeStart = LocalDate.now(clock),
            rangeEnd = LocalDate.now(clock),
            limit = 10,
            currencyPairs = setOf(),
            sides = setOf()
        )
        // when
        val result = repository.find(givenQuery)
        // then
        result shouldContainExactly listOf(givenOrder)
    }
    
    @Test
    fun `return with limit in query`() {
        // given
        val givenOrders = (1..5).mapIndexed { index, _ ->
            OrderMother.of(updatedAt = expectedDate.plus(index.toLong(), ChronoUnit.MINUTES))
        }
        givenOrders.forEach { repository.save(it) }
        val givenQuery = GetRecentOrdersQueryMother.of(limit = 3, currencyPairs = setOf(), sides = setOf())
        // when
        val result = repository.find(givenQuery)
        // then
        result.size shouldBe 3
        result shouldContainExactly givenOrders.takeLast(3).reversed()
    }
    
    @Test
    fun `return orders sorted by updatedAt descending`() {
        // given
        val givenOrder1 = OrderMother.of(updatedAt = expectedDate)
        val givenOrder2 = OrderMother.of(updatedAt = expectedDate.plus(5, ChronoUnit.MINUTES))
        val givenOrder3 = OrderMother.of(updatedAt = expectedDate.plus(10, ChronoUnit.MINUTES))
        repository.save(givenOrder1)
        repository.save(givenOrder2)
        repository.save(givenOrder3)
        val givenQuery = GetRecentOrdersQueryMother.of(limit = 10, currencyPairs = setOf(), sides = setOf())
        // when
        val result = repository.find(givenQuery)
        // then
        result shouldContainExactly listOf(givenOrder3, givenOrder2, givenOrder1)
    }
    
    @Test
    fun `find active bids`() {
        // given
        val givenActiveBid = OrderMother.of(side = OrderSide.BUY, status = OrderStatus.OPEN)
        val givenFilledBid = OrderMother.of(side = OrderSide.BUY, status = OrderStatus.FILLED)
        val givenActiveAsk = OrderMother.of(side = OrderSide.SELL, status = OrderStatus.OPEN)
        repository.save(givenActiveBid)
        repository.save(givenFilledBid)
        repository.save(givenActiveAsk)
        // when
        val result = repository.findBids()
        // then
        result shouldContainExactly listOf(givenActiveBid)
    }
    
    @Test
    fun `find active asks`() {
        // given
        val givenActiveAsk = OrderMother.of(side = OrderSide.SELL, status = OrderStatus.OPEN)
        val givenFilledAsk = OrderMother.of(side = OrderSide.SELL, status = OrderStatus.FILLED)
        val givenActiveBid = OrderMother.of(side = OrderSide.BUY, status = OrderStatus.OPEN)
        repository.save(givenActiveAsk)
        repository.save(givenFilledAsk)
        repository.save(givenActiveBid)
        // when
        val result = repository.findAsks()
        // then
        result shouldContainExactly listOf(givenActiveAsk)
    }
    
    @Test
    fun `return null when order not found`() {
        // given
        val givenId = TestData.uuidStr()
        // when
        repository.findById(givenId) shouldBe null
    }
    
    @Test
    fun `return order when found`() {
        // given
        val givenOrder = OrderMother.of()
        repository.save(givenOrder)
        // when
        val result = repository.findById(givenOrder.id)
        // then
        result shouldBe givenOrder
    }
}