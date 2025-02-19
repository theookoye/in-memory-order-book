package za.co.theookoye.domain.command.handler

import com.google.inject.Inject
import io.github.oshai.kotlinlogging.KotlinLogging
import za.co.theookoye.domain.command.GetRecentOrdersQuery
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.port.OrderRepositoryPort

private val log = KotlinLogging.logger { }

class GetRecentOrdersQueryHandler @Inject constructor(
    private val repository: OrderRepositoryPort,
) {
    
    fun handle(query: GetRecentOrdersQuery): List<Order> {
        return try {
            repository.find(query)
        } catch (e: Exception) {
            when (e) {
                is TradeOrderException -> throw e
                else -> {
                    val message = "Failed to get recent orders"
                    log.error(e) { message }
                    throw TradeOrderException(message)
                }
            }
        }
    }
}