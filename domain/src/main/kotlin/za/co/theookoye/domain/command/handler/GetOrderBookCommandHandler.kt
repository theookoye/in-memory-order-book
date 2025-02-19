package za.co.theookoye.domain.command.handler

import com.google.inject.Inject
import io.github.oshai.kotlinlogging.KotlinLogging
import za.co.theookoye.domain.command.GetOrderBookCommand
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.model.OrderBook
import za.co.theookoye.domain.port.OrderBookServicePort

private val log = KotlinLogging.logger { }

class GetOrderBookCommandHandler @Inject constructor(
    private val service: OrderBookServicePort,
) {
    
    fun handle(command: GetOrderBookCommand): OrderBook {
        return try {
            service.getOrderBook(currencyPair = command.currencyPair)
        } catch (e: Exception) {
            when (e) {
                is TradeOrderException -> throw e
                else -> {
                    val message = "Failed to get order book for currency pair: ${command.currencyPair}"
                    log.error(e) { message }
                    throw TradeOrderException(message)
                }
            }
        }
    }
}