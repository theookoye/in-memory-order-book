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
import za.co.theookoye.domain.mother.GetRecentOrdersQueryMother
import za.co.theookoye.domain.mother.OrderMother
import za.co.theookoye.domain.mother.TestData
import za.co.theookoye.domain.port.OrderRepositoryPort

@ExtendWith(MockKExtension::class)
class GetRecentOrdersQueryHandlerShould {
    
    @InjectMockKs
    private lateinit var handler: GetRecentOrdersQueryHandler
    
    @RelaxedMockK
    private lateinit var repository: OrderRepositoryPort
    private val foundOrder = OrderMother.of()
    
    @BeforeEach
    fun setUp() {
        every { repository.find(any()) } returns listOf(foundOrder)
    }
    
    @AfterEach
    fun tearDown() {
        confirmVerified(repository)
    }
    
    @Test
    fun `successfully get recent orders by query`() {
        // given
        val givenQuery = GetRecentOrdersQueryMother.of(limit = 5)
        // when
        val result = handler.handle(query = givenQuery)
        // then
        result shouldContainAll listOf(foundOrder)
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
    
    @Test
    fun `successfully get recent orders by range`() {
        // given
        val givenQuery = GetRecentOrdersQueryMother.of(
            rangeStart = TestData.past().toLocalDate(),
            rangeEnd = TestData.future().toLocalDate()
        )
        val founderOrders = listOf(OrderMother.of(), OrderMother.of())
        every { repository.find(any()) } returns founderOrders
        // when
        val result = handler.handle(query = givenQuery)
        // then
        result shouldContainAll founderOrders
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
    
    @Test
    fun `forward error if repository throws an exception when getting recent orders by query`() {
        // given
        val givenQuery = GetRecentOrdersQueryMother.of(limit = 5)
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
    fun `forward tradeOrderException if repository throws an exception when getting recent orders by query`() {
        // given
        val givenQuery = GetRecentOrdersQueryMother.of(limit = 5)
        every { repository.find(any()) } throws RuntimeException(TestData.string())
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(query = givenQuery) }
        // then
        exception shouldBe TradeOrderException("Failed to get recent orders")
        verify(exactly = 1) {
            repository.find(query = givenQuery)
        }
    }
}