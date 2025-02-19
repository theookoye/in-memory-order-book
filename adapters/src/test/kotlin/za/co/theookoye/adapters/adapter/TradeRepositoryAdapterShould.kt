package za.co.theookoye.adapters.adapter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.adapters.mother.GetRecentTradesQueryMother
import za.co.theookoye.adapters.mother.TestData
import za.co.theookoye.adapters.mother.TradeMother
import za.co.theookoye.domain.exception.ConcurrentUpdateException
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class TradeRepositoryAdapterShould {
    
    private lateinit var repository: TradeRepositoryAdapter
    private val expectedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    private val clock = Clock.fixed(expectedDate, ZoneId.systemDefault())
    
    @BeforeEach
    fun setup() {
        repository = TradeRepositoryAdapter(clock)
    }
    
    @Test
    fun `save new trade successfully`() {
        // given
        val givenTrade = TradeMother.of(id = "trade1", version = 0)
        // when
        repository.save(givenTrade)
        // then
        repository.findById("trade1") shouldBe givenTrade
    }
    
    @Test
    fun `increment version when updating existing trade with matching version`() {
        // given
        val givenTrade = TradeMother.of(id = "trade1", version = 1)
        repository.save(givenTrade)
        // when
        val updatedTrade = givenTrade.copy(price = givenTrade.price.plus(BigDecimal.TEN))
        repository.save(updatedTrade)
        // then
        repository.findById("trade1")?.version shouldBe 2
    }
    
    @Test
    fun `throw ConcurrentUpdateException when versions don't match`() {
        // given
        val givenTrade = TradeMother.of(id = "trade1", version = 1)
        repository.save(givenTrade)
        // when - then
        shouldThrow<ConcurrentUpdateException> {
            repository.save(givenTrade.copy(version = 2))
        }
    }
    
    @Test
    fun `find trades by currency pair`() {
        // given
        val givenBtcTrade1 = TradeMother.of(currencyPair = "BTC" + TestData.currencyCode())
        val givenBtcTrade2 = TradeMother.of(currencyPair = givenBtcTrade1.currencyPair)
        val givenEthTrade = TradeMother.of(currencyPair = "ETH" + TestData.currencyCode())
        repository.save(givenBtcTrade1)
        repository.save(givenBtcTrade2)
        repository.save(givenEthTrade)
        val givenQuery = GetRecentTradesQueryMother.of(limit = 100, currencyPairs = setOf(givenEthTrade.currencyPair))
        // when
        val result = repository.find(givenQuery)
        // then
        result shouldContainExactly listOf(givenEthTrade)
    }
    
    @Test
    fun `find trades within date range`() {
        // given
        val givenPastTrade = TradeMother.of(tradedAt = expectedDate.minus(1, ChronoUnit.DAYS))
        val givenTrade = TradeMother.of(tradedAt = expectedDate)
        repository.save(givenPastTrade)
        repository.save(givenTrade)
        val givenQuery = GetRecentTradesQueryMother.of(
            rangeStart = LocalDate.now(clock),
            rangeEnd = LocalDate.now(clock),
            limit = 10,
            currencyPairs = setOf()
        )
        // when
        val result = repository.find(givenQuery)
        // then
        result shouldContainExactly listOf(givenTrade)
    }
    
    @Test
    fun `return with limit in query`() {
        // given
        val givenTrades = (1..5).mapIndexed { index, _ ->
            TradeMother.of(tradedAt = expectedDate.plus(index.toLong(), ChronoUnit.MINUTES))
        }
        givenTrades.forEach { repository.save(it) }
        val givenQuery = GetRecentTradesQueryMother.of(limit = 3, currencyPairs = setOf())
        // when
        val result = repository.find(givenQuery)
        // then
        result.size shouldBe 3
        result shouldContainExactly givenTrades.takeLast(3).reversed()
    }
    
    @Test
    fun `return trades sorted by tradedAt descending`() {
        // given
        val givenTrade1 = TradeMother.of(tradedAt = expectedDate)
        val givenTrade2 = TradeMother.of(tradedAt = expectedDate.plus(5, ChronoUnit.MINUTES))
        val givenTrade3 = TradeMother.of(tradedAt = expectedDate.plus(10, ChronoUnit.MINUTES))
        repository.save(givenTrade1)
        repository.save(givenTrade2)
        repository.save(givenTrade3)
        val givenQuery = GetRecentTradesQueryMother.of(limit = 10, currencyPairs = setOf())
        // when
        val result = repository.find(givenQuery)
        // then
        result shouldContainExactly listOf(givenTrade3, givenTrade2, givenTrade1)
    }
    
    @Test
    fun `return null when trade not found`() {
        // given
        val givenId = TestData.uuidStr()
        // when
        repository.findById(givenId) shouldBe null
    }
    
    @Test
    fun `return trade when found`() {
        // given
        val givenTrade = TradeMother.of()
        repository.save(givenTrade)
        // when
        val result = repository.findById(givenTrade.id)
        // then
        result shouldBe givenTrade
    }
}