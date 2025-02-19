package za.co.theookoye.dto

import java.time.Instant

data class TradeV1(
    val id: String,
    val sequenceId: Long,
    val price: String,
    val quantity: String,
    val quoteVolume: String,
    val currencyPair: String,
    val takerSide: String,
    val tradedAt: Instant,
)
