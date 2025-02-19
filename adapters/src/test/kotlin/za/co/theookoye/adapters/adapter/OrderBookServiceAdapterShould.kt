package za.co.theookoye.adapters.adapter

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.adapters.mother.OrderMother
import za.co.theookoye.adapters.mother.TestData
import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.OrderStatus
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class OrderBookServiceAdapterShould {
    
    private lateinit var service: OrderBookServiceAdapter
    private lateinit var orderRepository: OrderRepositoryAdapter
    private val expectedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    private val clock = Clock.fixed(expectedDate, ZoneId.systemDefault())
    private val currencyPair = "BTC" + TestData.currencyCode()
    
    @BeforeEach
    fun setup() {
        orderRepository = OrderRepositoryAdapter(clock)
        service = OrderBookServiceAdapter(orderRepository, clock)
    }
    
    @AfterEach
    fun tearDown() {
        orderRepository.clearAll()
    }
    
    @Test
    fun `add order to repository`() {
        // given
        val givenOrder = OrderMother.of()
        // when
        service.addOrder(givenOrder)
        // then
        orderRepository.findById(givenOrder.id) shouldBe givenOrder
    }
    
    @Test
    fun `get order book for currency pair`() {
        // given
        val givenBid = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.BUY,
            status = OrderStatus.OPEN
        )
        val givenAsk = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.SELL,
            status = OrderStatus.OPEN
        )
        val givenOtherPairOrder = OrderMother.of(
            currencyPair = TestData.currencyCode() + TestData.currencyCode(),
            status = OrderStatus.OPEN
        )
        
        orderRepository.save(givenBid)
        orderRepository.save(givenAsk)
        orderRepository.save(givenOtherPairOrder)
        // when
        val result = service.getOrderBook(currencyPair)
        // then
        result.bids.size shouldBe 1
        result.asks.size shouldBe 1
        result.lastChange shouldBe expectedDate
    }
    
    @Test
    fun `match buy order with best ask price`() {
        // given
        val givenBuyOrder = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.BUY,
            quantity = BigDecimal("1"),
            price = BigDecimal("50000"),
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        val givenSellOrder = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.SELL,
            quantity = BigDecimal("1"),
            price = BigDecimal("49000"),
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        
        orderRepository.save(givenBuyOrder)
        orderRepository.save(givenSellOrder)
        // when
        val trades = service.matchBuyOrder(givenBuyOrder)
        // then
        trades.size shouldBe 1
        with(trades.first()) {
            price shouldBe givenSellOrder.price
            quantity shouldBe givenBuyOrder.quantity
            currencyPair shouldBe givenBuyOrder.currencyPair
            takerSide shouldBe OrderSide.BUY
            quoteVolume shouldBe (quantity * price)
        }
        orderRepository.findById(givenBuyOrder.id)?.status shouldBe OrderStatus.FILLED
        orderRepository.findById(givenSellOrder.id)?.status shouldBe OrderStatus.FILLED
    }
    
    @Test
    fun `match sell order with best bid price`() {
        // given
        val givenSellOrder = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.SELL,
            quantity = BigDecimal("1"),
            price = BigDecimal("49000"),
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        val givenBuyOrder = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.BUY,
            quantity = BigDecimal("1"),
            price = BigDecimal("50000"),
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        
        orderRepository.save(givenSellOrder)
        orderRepository.save(givenBuyOrder)
        // when
        val trades = service.matchSellOrder(givenSellOrder)
        // then
        trades.size shouldBe 1
        with(trades.first()) {
            price shouldBe givenBuyOrder.price
            quantity shouldBe givenSellOrder.quantity
            currencyPair shouldBe givenSellOrder.currencyPair
            takerSide shouldBe OrderSide.SELL
            quoteVolume shouldBe (quantity * price)
        }
        orderRepository.findById(givenSellOrder.id)?.status shouldBe OrderStatus.FILLED
        orderRepository.findById(givenBuyOrder.id)?.status shouldBe OrderStatus.FILLED
    }
    
    @Test
    fun `handle partial fills for orders`() {
        // given
        val givenBuyOrder = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.BUY,
            quantity = BigDecimal("2"),
            price = BigDecimal("50000"),
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        val givenSellOrder = OrderMother.of(
            currencyPair = currencyPair,
            side = OrderSide.SELL,
            quantity = BigDecimal("1"),
            price = BigDecimal("49000"),
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        
        orderRepository.save(givenBuyOrder)
        orderRepository.save(givenSellOrder)
        // when
        val trades = service.matchBuyOrder(givenBuyOrder)
        // then
        trades.size shouldBe 1
        val updatedBuyOrder = orderRepository.findById(givenBuyOrder.id)
        updatedBuyOrder?.status shouldBe OrderStatus.PARTIALLY_FILLED
        updatedBuyOrder?.filledQuantity shouldBe BigDecimal("1")
        val updatedSellOrder = orderRepository.findById(givenSellOrder.id)
        updatedSellOrder?.status shouldBe OrderStatus.FILLED
        updatedSellOrder?.filledQuantity shouldBe BigDecimal("1")
    }
    
    @Test
    fun `not match orders when no matching orders exist`() {
        // given
        val givenBuyOrder = OrderMother.of(
            side = OrderSide.BUY,
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        
        orderRepository.save(givenBuyOrder)
        // when
        val trades = service.matchBuyOrder(givenBuyOrder)
        // then
        trades.shouldBeEmpty()
    }
    
    @Test
    fun `not match orders with non-OPEN status`() {
        // given
        val givenBuyOrder = OrderMother.of(
            side = OrderSide.BUY,
            status = OrderStatus.OPEN,
            filledQuantity = BigDecimal.ZERO
        )
        val givenFilledSellOrder = OrderMother.of(
            side = OrderSide.SELL,
            status = OrderStatus.FILLED,
            filledQuantity = BigDecimal("1")
        )
        
        orderRepository.save(givenBuyOrder)
        orderRepository.save(givenFilledSellOrder)
        // when
        val trades = service.matchBuyOrder(givenBuyOrder)
        // then
        trades.shouldBeEmpty()
    }
}