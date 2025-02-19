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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import za.co.theookoye.domain.command.GetOrderCommand
import za.co.theookoye.domain.exception.ModelNotFoundException
import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.mother.OrderMother
import za.co.theookoye.domain.mother.TestData
import za.co.theookoye.domain.port.OrderRepositoryPort
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(MockKExtension::class)
class GetOrderCommandHandlerShould {
    
    @InjectMockKs
    private lateinit var handler: GetOrderCommandHandler
    
    @RelaxedMockK
    private lateinit var repository: OrderRepositoryPort
    
    @RelaxedMockK
    private lateinit var clock: Clock
    private val expectedDate = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    
    @BeforeEach
    internal fun setUp() {
        every { clock.instant() } returns expectedDate
    }
    
    @AfterEach
    internal fun tearDown() {
        confirmVerified(repository)
    }
    
    @Test
    fun `successfully get an order by id`() {
        // given
        val givenCommand = GetOrderCommand(id = TestData.uuidStr())
        val foundOrder = OrderMother.of()
        
        every { repository.findRequired(any()) } returns foundOrder
        // when
        val result = handler.handle(command = givenCommand)
        // then
        result shouldBe foundOrder
        verify(exactly = 1) {
            repository.findRequired(id = givenCommand.id)
        }
    }
    
    @Test
    fun `forward error when order is not found by id`() {
        // given
        val givenCommand = GetOrderCommand(id = TestData.uuidStr())
        val expectedException = ModelNotFoundException(Order::class, givenCommand.id)
        
        
        every { repository.findRequired(any()) } throws expectedException
        // when
        val exception = shouldThrow<ModelNotFoundException> { handler.handle(command = givenCommand) }
        // then
        exception shouldBe expectedException
        verify(exactly = 1) {
            repository.findRequired(id = givenCommand.id)
        }
    }
}