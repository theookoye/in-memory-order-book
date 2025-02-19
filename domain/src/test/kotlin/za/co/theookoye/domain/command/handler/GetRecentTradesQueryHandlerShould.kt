package za.co.theookoye.domain.command.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.mother.GetRecentTradesQueryMother
import za.co.theookoye.domain.mother.TestData
import za.co.theookoye.domain.mother.TradeMother
import za.co.theookoye.domain.port.TradeRepositoryPort
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class GetRecentTradesQueryHandlerShould {
    
    @InjectMockKs
    private lateinit var handler: GetRecentTradesQueryHandler
    
    @RelaxedMockK
    private lateinit var repository: TradeRepositoryPort
    
    @RelaxedMockK
    private lateinit var clock: Clock
    private val expectedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    private val foundTrade = TradeMother.of()
    
    @BeforeEach
    fun setUp() {
        every { clock.instant() } returns expectedDate
        every { repository.find(any()) } returns listOf(foundTrade)
    }
    
    @AfterEach
    fun tearDown() {
        confirmVerified(repository)
    }
    
    @Test
    fun `successfully get recent trades by query`() {
        // given
        val givenQuery = GetRecentTradesQueryMother.of(limit = 5)
        // when
        val result = handler.handle(query = givenQuery)
        // then
        result shouldContainAll listOf(foundTrade)
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
    
    @Test
    fun `successfully get recent trades by range`() {
        // given
        val givenQuery = GetRecentTradesQueryMother.of(
            rangeStart = TestData.past().toLocalDate(),
            rangeEnd = TestData.future().toLocalDate()
        )
        val founderTrades = listOf(TradeMother.of(), TradeMother.of())
        every { repository.find(any()) } returns founderTrades
        // when
        val result = handler.handle(query = givenQuery)
        // then
        result shouldContainAll founderTrades
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
    
    @Test
    fun `forward error if repository throws an exception when getting recent trades by query`() {
        // given
        val givenQuery = GetRecentTradesQueryMother.of(limit = 5)
        val expectedException = TradeOrderException(TestData.string())
        every { repository.find(any()) } throws expectedException
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(query = givenQuery) }
        // then
        exception shouldBe expectedException
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
    
    @Test
    fun `forward tradeOrderException if repository throws an exception when getting recent trades by query`() {
        // given
        val givenQuery = GetRecentTradesQueryMother.of(limit = 5)
        every { repository.find(any()) } throws RuntimeException(TestData.string())
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(query = givenQuery) }
        // then
        exception shouldBe TradeOrderException("Failed to get recent trades")
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
}