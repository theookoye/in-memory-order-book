package za.co.theookoye.domain.command.handler

import com.google.inject.Inject
import io.github.oshai.kotlinlogging.KotlinLogging
import za.co.theookoye.domain.command.SubmitOrderLimitCommand
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.model.*
import za.co.theookoye.domain.port.OrderBookServicePort
import za.co.theookoye.domain.port.OrderRepositoryPort
import za.co.theookoye.domain.port.TradeRepositoryPort
import java.time.Clock
import java.time.Instant

private val log = KotlinLogging.logger { }

class SubmitOrderLimitCommandHandler @Inject constructor(
    private val orderBookService: OrderBookServicePort,
    private val tradeRepository: TradeRepositoryPort,
    private val orderRepository: OrderRepositoryPort,
    private val clock: Clock,
) {
    
    fun handle(command: SubmitOrderLimitCommand): SubmitOrderResult {
        return try {
            val order = Order(
                side = command.side,
                quantity = command.quantity,
                price = command.price,
                currencyPair = command.currencyPair,
                status = OrderStatus.OPEN,
                createdAt = Instant.now(clock),
                updatedAt = Instant.now(clock),
            )
            
            orderBookService.addOrder(order = order)
            val trades = matchOrder(order = order)
            saveTrades(trades = trades)
            val latestOrder = orderRepository.findRequired(id = order.id)
            
            SubmitOrderResult(
                order = latestOrder,
                trades = trades,
            )
        } catch (e: Exception) {
            when (e) {
                is TradeOrderException -> throw e
                else -> {
                    val message = "Failed to submit order limit"
                    log.error(e) { message }
                    throw TradeOrderException(message)
                }
            }
        }
    }
    
    private fun matchOrder(order: Order): List<Trade> =
        when (order.side) {
            OrderSide.BUY -> orderBookService.matchBuyOrder(order)
            OrderSide.SELL -> orderBookService.matchSellOrder(order)
        }
    
    private fun saveTrades(trades: List<Trade>) {
        trades.forEach { trade ->
            runCatching {
                tradeRepository.save(trade)
            }.onFailure { e ->
                log.error { "Failed to save trade $trade: ${e.message}" }
            }
        }
    }
}