package za.co.theookoye.domain.command.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.domain.command.GetOrderBookCommand
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.mother.OrderBookMother
import za.co.theookoye.domain.mother.TestData
import za.co.theookoye.domain.port.OrderBookServicePort

@ExtendWith(MockKExtension::class)
class GetOrderBookCommandHandlerShould {
    
    @InjectMockKs
    private lateinit var handler: GetOrderBookCommandHandler
    
    @RelaxedMockK
    private lateinit var service: OrderBookServicePort
    
    @AfterEach
    fun tearDown() {
        confirmVerified(service)
    }
    
    @Test
    fun `successfully get latest snapshot of order book by currency pair`() {
        // given
        val givenCommand = GetOrderBookCommand(currencyPair = TestData.currencyCode() + TestData.currencyCode())
        val foundOrderBook = OrderBookMother.of()
        
        every { service.getOrderBook(any()) } returns foundOrderBook
        // when
        val result = handler.handle(command = givenCommand)
        // then
        result shouldBe foundOrderBook
        verify(exactly = 1) {
            service.getOrderBook(currencyPair = givenCommand.currencyPair)
        }
    }
    
    @Test
    fun `forward tradeOrderException if service throws an exception when trying to get latest snapshot of order book by currency pair`() {
        // given
        val givenCommand = GetOrderBookCommand(currencyPair = TestData.currencyCode() + TestData.currencyCode())
        val expectedException = RuntimeException(TestData.string())
        
        every { service.getOrderBook(any()) } throws expectedException
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(command = givenCommand) }
        // then
        exception shouldBe TradeOrderException("Failed to get order book for currency pair: ${givenCommand.currencyPair}")
        verify(exactly = 1) {
            service.getOrderBook(currencyPair = givenCommand.currencyPair)
        }
    }
    
    @Test
    fun `forward error if service throws an exception when trying to get latest snapshot of order book by currency pair`() {
        // given
        val givenCommand = GetOrderBookCommand(currencyPair = TestData.currencyCode() + TestData.currencyCode())
        val expectedException = TradeOrderException(TestData.string())
        
        every { service.getOrderBook(any()) } throws expectedException
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(command = givenCommand) }
        // then
        exception shouldBe expectedException
        verify(exactly = 1) {
            service.getOrderBook(currencyPair = givenCommand.currencyPair)
        }
    }
}