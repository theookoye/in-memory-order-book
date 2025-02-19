package za.co.theookoye.domain.port

import za.co.theookoye.domain.command.GetRecentTradesQuery
import za.co.theookoye.domain.model.Trade

interface TradeRepositoryPort {
    
    fun save(trade: Trade)
    fun find(query: GetRecentTradesQuery): List<Trade>
    fun findById(id: String): Trade?
}