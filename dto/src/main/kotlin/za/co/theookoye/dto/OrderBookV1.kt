package za.co.theookoye.dto

import java.time.Instant

data class OrderBookV1(
    val asks: List<OrderBookEntryV1>,
    val bids: List<OrderBookEntryV1>,
    val lastChange: Instant,
    val sequenceNumber: Long,
)

data class OrderBookEntryV1(
    val side: String,
    val quantity: String,
    val price: String,
    val currencyPair: String,
    val orderCount: Int,
)
