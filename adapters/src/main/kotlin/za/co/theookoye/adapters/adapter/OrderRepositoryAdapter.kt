package za.co.theookoye.adapters.adapter

import com.google.inject.Inject
import za.co.theookoye.domain.command.GetRecentOrdersQuery
import za.co.theookoye.domain.exception.ConcurrentUpdateException
import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.OrderStatus
import za.co.theookoye.domain.port.OrderRepositoryPort
import java.time.Clock

class OrderRepositoryAdapter @Inject constructor(
    private val clock: Clock,
) : OrderRepositoryPort {
    
    private val orders = mutableMapOf<String, Order>()
    
    override fun save(order: Order) {
        val existingOrder = orders[order.id]
        when {
            existingOrder == null -> orders[order.id] = order
            existingOrder.version != order.version -> {
                throw ConcurrentUpdateException(
                    Order::class,
                    existingOrder.version
                )
            }
            
            else -> orders[order.id] = order.copy(version = order.version + 1)
        }
    }
    
    override fun findById(id: String): Order? {
        return orders[id]
    }
    
    override fun findBids(): List<Order> {
        return orders.values.filter { it.side == OrderSide.BUY && it.status != OrderStatus.FILLED }
    }
    
    override fun findAsks(): List<Order> {
        return orders.values.filter { it.side == OrderSide.SELL && it.status != OrderStatus.FILLED }
    }
    
    override fun find(query: GetRecentOrdersQuery): List<Order> {
        val filteredOrders = orders.values.filter { order ->
            (query.currencyPairs.isEmpty() || query.currencyPairs.contains(order.currencyPair)) &&
                    (query.sides.isEmpty() || query.sides.contains(order.side)) &&
                    (query.rangeStart == null || order.updatedAt >= query.rangeStart?.atStartOfDay(clock.zone)
                        ?.toInstant()) &&
                    (query.rangeEnd == null || order.updatedAt <= query.rangeEnd?.plusDays(1)?.atStartOfDay(clock.zone)
                        ?.toInstant())
        }
        val sortedOrders = filteredOrders.sortedByDescending { it.updatedAt }
        return sortedOrders.take(query.limit)
    }
    
    override fun clearAll() {
        return orders.clear()
    }
}