package za.co.theookoye.adapters.adapter

import com.google.inject.Inject
import za.co.theookoye.domain.command.GetRecentTradesQuery
import za.co.theookoye.domain.exception.ConcurrentUpdateException
import za.co.theookoye.domain.model.Trade
import za.co.theookoye.domain.port.TradeRepositoryPort
import java.time.Clock

class TradeRepositoryAdapter @Inject constructor(
    private val clock: Clock,
) : TradeRepositoryPort {
    
    private val trades = mutableMapOf<String, Trade>()
    
    override fun save(trade: Trade) {
        val existingTrade = trades[trade.id]
        when {
            existingTrade == null -> trades[trade.id] = trade
            existingTrade.version != trade.version -> {
                throw ConcurrentUpdateException(
                    Trade::class,
                    existingTrade.version
                )
            }
            
            else -> trades[trade.id] = trade.copy(version = trade.version + 1)
        }
    }
    
    override fun find(query: GetRecentTradesQuery): List<Trade> {
        val filteredTrades = trades.values.filter { trade ->
            (query.currencyPairs.isEmpty() || query.currencyPairs.contains(trade.currencyPair)) &&
                    (query.rangeStart == null || trade.tradedAt >= query.rangeStart?.atStartOfDay(clock.zone)
                        ?.toInstant()) &&
                    (query.rangeEnd == null || trade.tradedAt <= query.rangeEnd?.plusDays(1)?.atStartOfDay(clock.zone)
                        ?.toInstant())
        }
        val sortedTrades = filteredTrades.sortedByDescending { it.tradedAt }
        return sortedTrades.take(query.limit)
    }
    
    override fun findById(id: String): Trade? {
        return trades[id]
    }
}