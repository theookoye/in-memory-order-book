package za.co.theookoye.domain.port

import za.co.theookoye.domain.command.GetRecentOrdersQuery
import za.co.theookoye.domain.exception.ModelNotFoundException
import za.co.theookoye.domain.model.Order

interface OrderRepositoryPort {
    
    fun save(order: Order)
    fun findBids(): List<Order>
    fun findAsks(): List<Order>
    fun findById(id: String): Order?
    fun findRequired(id: String): Order = findById(id) ?: throw ModelNotFoundException(Order::class, id)
    fun find(query: GetRecentOrdersQuery): List<Order>
    fun clearAll()
}