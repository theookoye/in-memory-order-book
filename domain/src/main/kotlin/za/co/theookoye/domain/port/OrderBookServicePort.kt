package za.co.theookoye.domain.port

import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.model.OrderBook
import za.co.theookoye.domain.model.Trade

interface OrderBookServicePort {
    
    fun addOrder(order: Order)
    fun getOrderBook(currencyPair: String): OrderBook
    fun matchBuyOrder(order: Order): List<Trade>
    fun matchSellOrder(order: Order): List<Trade>
}