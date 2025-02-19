package za.co.theookoye.domain.command.handler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.domain.exception.ConcurrentUpdateException
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.OrderStatus
import za.co.theookoye.domain.model.SubmitOrderResult
import za.co.theookoye.domain.mother.OrderMother
import za.co.theookoye.domain.mother.SubmitOrderLimitCommandMother
import za.co.theookoye.domain.mother.TestData
import za.co.theookoye.domain.mother.TradeMother
import za.co.theookoye.domain.port.OrderBookServicePort
import za.co.theookoye.domain.port.OrderRepositoryPort
import za.co.theookoye.domain.port.TradeRepositoryPort
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class SubmitOrderLimitCommandHandlerShould {
    
    @InjectMockKs
    private lateinit var handler: SubmitOrderLimitCommandHandler
    
    @RelaxedMockK
    private lateinit var service: OrderBookServicePort
    
    @RelaxedMockK
    private lateinit var orderRepository: OrderRepositoryPort
    
    @RelaxedMockK
    private lateinit var tradeRepository: TradeRepositoryPort
    
    @RelaxedMockK
    private lateinit var clock: Clock
    private val expectedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    
    @BeforeEach
    fun setUp() {
        every { clock.instant() } returns expectedDate
    }
    
    @AfterEach
    fun tearDown() {
        confirmVerified(service, orderRepository, tradeRepository)
    }
    
    @Test
    fun `successfully submit buy order`() {
        // given
        val givenCommand = SubmitOrderLimitCommandMother.of(side = OrderSide.BUY)
        val givenOrder = OrderMother.of(
            side = givenCommand.side,
            quantity = givenCommand.quantity,
            price = givenCommand.price,
            currencyPair = givenCommand.currencyPair,
        )
        val givenTrade1 = TradeMother.of()
        val givenTrade2 = TradeMother.of()
        val orderSlot = CapturingSlot<Order>()
        
        every { service.addOrder(any()) } just runs
        every { service.matchBuyOrder(any()) } returns listOf(givenTrade1, givenTrade2)
        every { orderRepository.findRequired(any()) } returns givenOrder
        every { tradeRepository.save(any()) } just runs
        // when
        val result = handler.handle(command = givenCommand)
        // then
        result shouldBe SubmitOrderResult(
            order = givenOrder,
            trades = listOf(givenTrade1, givenTrade2),
        )
        verify(exactly = 1) {
            service.addOrder(capture(orderSlot))
        }
        orderSlot.captured.version shouldBe 0
        orderSlot.captured.side shouldBe givenCommand.side
        orderSlot.captured.quantity shouldBe givenCommand.quantity
        orderSlot.captured.filledQuantity shouldBe BigDecimal.ZERO
        orderSlot.captured.price shouldBe givenCommand.price
        orderSlot.captured.currencyPair shouldBe givenCommand.currencyPair
        orderSlot.captured.status shouldBe OrderStatus.OPEN
        orderSlot.captured.createdAt shouldBe expectedDate
        orderSlot.captured.updatedAt shouldBe expectedDate
        
        verify(exactly = 1) {
            service.matchBuyOrder(orderSlot.captured)
        }
        
        verify(exactly = 1) {
            orderRepository.findRequired(id = orderSlot.captured.id)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade1)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade2)
        }
    }
    
    @Test
    fun `successfully submit sell order`() {
        // given
        val givenCommand = SubmitOrderLimitCommandMother.of(side = OrderSide.SELL)
        val givenOrder = OrderMother.of(
            side = givenCommand.side,
            quantity = givenCommand.quantity,
            price = givenCommand.price,
            currencyPair = givenCommand.currencyPair,
        )
        val givenTrade1 = TradeMother.of()
        val givenTrade2 = TradeMother.of()
        val orderSlot = CapturingSlot<Order>()
        
        every { service.addOrder(any()) } just runs
        every { service.matchSellOrder(any()) } returns listOf(givenTrade1, givenTrade2)
        every { orderRepository.findRequired(any()) } returns givenOrder
        every { tradeRepository.save(any()) } just runs
        // when
        val result = handler.handle(command = givenCommand)
        // then
        result shouldBe SubmitOrderResult(
            order = givenOrder,
            trades = listOf(givenTrade1, givenTrade2),
        )
        verify(exactly = 1) {
            service.addOrder(capture(orderSlot))
        }
        orderSlot.captured.version shouldBe 0
        orderSlot.captured.side shouldBe givenCommand.side
        orderSlot.captured.quantity shouldBe givenCommand.quantity
        orderSlot.captured.filledQuantity shouldBe BigDecimal.ZERO
        orderSlot.captured.price shouldBe givenCommand.price
        orderSlot.captured.currencyPair shouldBe givenCommand.currencyPair
        orderSlot.captured.status shouldBe OrderStatus.OPEN
        orderSlot.captured.createdAt shouldBe expectedDate
        orderSlot.captured.updatedAt shouldBe expectedDate
        
        verify(exactly = 1) {
            service.matchSellOrder(orderSlot.captured)
        }
        
        verify(exactly = 1) {
            orderRepository.findRequired(id = orderSlot.captured.id)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade1)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade2)
        }
    }
    
    @Test
    fun `successfully submit order with no trades if no matches are found`() {
        // given
        val givenCommand = SubmitOrderLimitCommandMother.of(side = OrderSide.SELL)
        val givenOrder = OrderMother.of(
            side = givenCommand.side,
            quantity = givenCommand.quantity,
            price = givenCommand.price,
            currencyPair = givenCommand.currencyPair,
        )
        val orderSlot = CapturingSlot<Order>()
        
        every { service.addOrder(any()) } just runs
        every { service.matchSellOrder(any()) } returns listOf()
        every { orderRepository.findRequired(any()) } returns givenOrder
        every { tradeRepository.save(any()) } just runs
        // when
        val result = handler.handle(command = givenCommand)
        // then
        result shouldBe SubmitOrderResult(
            order = givenOrder,
            trades = listOf(),
        )
        verify(exactly = 1) {
            service.addOrder(capture(orderSlot))
        }
        orderSlot.captured.version shouldBe 0
        orderSlot.captured.side shouldBe givenCommand.side
        orderSlot.captured.quantity shouldBe givenCommand.quantity
        orderSlot.captured.filledQuantity shouldBe BigDecimal.ZERO
        orderSlot.captured.price shouldBe givenCommand.price
        orderSlot.captured.currencyPair shouldBe givenCommand.currencyPair
        orderSlot.captured.status shouldBe OrderStatus.OPEN
        orderSlot.captured.createdAt shouldBe expectedDate
        orderSlot.captured.updatedAt shouldBe expectedDate
        
        verify(exactly = 1) {
            service.matchSellOrder(orderSlot.captured)
        }
        
        verify(exactly = 1) {
            orderRepository.findRequired(id = orderSlot.captured.id)
        }
    }
    
    @Test
    fun `not block the entire submit order when trade repository throws an exception`() {
        // given
        val givenCommand = SubmitOrderLimitCommandMother.of(side = OrderSide.SELL)
        val givenOrder = OrderMother.of(
            side = givenCommand.side,
            quantity = givenCommand.quantity,
            price = givenCommand.price,
            currencyPair = givenCommand.currencyPair,
        )
        val givenTrade1 = TradeMother.of()
        val givenTrade2 = TradeMother.of()
        val givenTrade3 = TradeMother.of()
        val orderSlot = CapturingSlot<Order>()
        
        every { service.addOrder(any()) } just runs
        every { service.matchSellOrder(any()) } returns listOf(givenTrade1, givenTrade2, givenTrade3)
        every { orderRepository.findRequired(any()) } returns givenOrder
        every { tradeRepository.save(any()) }.just(runs).andThenThrows(RuntimeException(TestData.string()))
            .andThenJust(runs)
        // when
        val result = handler.handle(command = givenCommand)
        // then
        result shouldBe SubmitOrderResult(
            order = givenOrder,
            trades = listOf(givenTrade1, givenTrade2, givenTrade3),
        )
        verify(exactly = 1) {
            service.addOrder(capture(orderSlot))
        }
        orderSlot.captured.version shouldBe 0
        orderSlot.captured.side shouldBe givenCommand.side
        orderSlot.captured.quantity shouldBe givenCommand.quantity
        orderSlot.captured.filledQuantity shouldBe BigDecimal.ZERO
        orderSlot.captured.price shouldBe givenCommand.price
        orderSlot.captured.currencyPair shouldBe givenCommand.currencyPair
        orderSlot.captured.status shouldBe OrderStatus.OPEN
        orderSlot.captured.createdAt shouldBe expectedDate
        orderSlot.captured.updatedAt shouldBe expectedDate
        
        verify(exactly = 1) {
            service.matchSellOrder(orderSlot.captured)
        }
        
        verify(exactly = 1) {
            orderRepository.findRequired(id = orderSlot.captured.id)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade1)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade2)
        }
        
        verify(exactly = 1) {
            tradeRepository.save(trade = givenTrade3)
        }
    }
    
    @Test
    fun `forward error when service throws an exception when submitting an order`() {
        // given
        val givenCommand = SubmitOrderLimitCommandMother.of(side = OrderSide.BUY)
        val orderSlot = CapturingSlot<Order>()
        val expectedException = TradeOrderException(TestData.string())
        
        every { service.addOrder(any()) } just runs
        every { service.matchBuyOrder(any()) } throws expectedException
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(command = givenCommand) }
        // then
        exception shouldBe expectedException
        verify(exactly = 1) {
            service.addOrder(capture(orderSlot))
        }
        orderSlot.captured.version shouldBe 0
        orderSlot.captured.side shouldBe givenCommand.side
        orderSlot.captured.quantity shouldBe givenCommand.quantity
        orderSlot.captured.filledQuantity shouldBe BigDecimal.ZERO
        orderSlot.captured.price shouldBe givenCommand.price
        orderSlot.captured.currencyPair shouldBe givenCommand.currencyPair
        orderSlot.captured.status shouldBe OrderStatus.OPEN
        orderSlot.captured.createdAt shouldBe expectedDate
        orderSlot.captured.updatedAt shouldBe expectedDate
        
        verify(exactly = 1) {
            service.matchBuyOrder(orderSlot.captured)
        }
    }
    
    @Test
    fun `forward tradeOrderException when service throws an exception when submitting an order`() {
        // given
        val givenCommand = SubmitOrderLimitCommandMother.of(side = OrderSide.BUY)
        val orderSlot = CapturingSlot<Order>()
        
        every { service.addOrder(any()) } just runs
        every { service.matchBuyOrder(any()) } throws ConcurrentUpdateException(Order::class, TestData.uuidStr())
        // when
        val exception = shouldThrow<TradeOrderException> { handler.handle(command = givenCommand) }
        // then
        exception shouldBe TradeOrderException("Failed to submit order limit")
        verify(exactly = 1) {
            service.addOrder(capture(orderSlot))
        }
        orderSlot.captured.version shouldBe 0
        orderSlot.captured.side shouldBe givenCommand.side
        orderSlot.captured.quantity shouldBe givenCommand.quantity
        orderSlot.captured.filledQuantity shouldBe BigDecimal.ZERO
        orderSlot.captured.price shouldBe givenCommand.price
        orderSlot.captured.currencyPair shouldBe givenCommand.currencyPair
        orderSlot.captured.status shouldBe OrderStatus.OPEN
        orderSlot.captured.createdAt shouldBe expectedDate
        orderSlot.captured.updatedAt shouldBe expectedDate
        
        verify(exactly = 1) {
            service.matchBuyOrder(orderSlot.captured)
        }
    }
}