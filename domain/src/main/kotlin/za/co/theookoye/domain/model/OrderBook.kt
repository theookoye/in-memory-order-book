package za.co.theookoye.domain.model

import java.math.BigDecimal
import java.time.Instant

data class OrderBook(
    val asks: List<OrderBookEntry>,
    val bids: List<OrderBookEntry>,
    val lastChange: Instant,
    val sequenceNumber: Long
)

data class OrderBookEntry(
    val side: OrderSide,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: String,
    val orderCount: Int
)
