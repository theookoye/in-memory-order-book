package za.co.theookoye.domain.command.handler

import com.google.inject.Inject
import io.github.oshai.kotlinlogging.KotlinLogging
import za.co.theookoye.domain.command.GetRecentTradesQuery
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.model.Trade
import za.co.theookoye.domain.port.TradeRepositoryPort

private val log = KotlinLogging.logger { }

class GetRecentTradesQueryHandler @Inject constructor(
    private val repository: TradeRepositoryPort,
) {
    
    fun handle(query: GetRecentTradesQuery): List<Trade> {
        return try {
            repository.find(query)
        } catch (e: Exception) {
            when (e) {
                is TradeOrderException -> throw e
                else -> {
                    val message = "Failed to get recent trades"
                    log.error(e) { message }
                    throw TradeOrderException(message)
                }
            }
        }
    }
}