package za.co.theookoye.domain.model

import java.math.BigDecimal
import java.time.Instant

data class Trade(
    val id: String,
    val sequenceId: Long,
    val version: Int,
    val price: BigDecimal,
    val quantity: BigDecimal,
    val quoteVolume: BigDecimal,
    val currencyPair: String,
    val takerSide: OrderSide,
    val tradedAt: Instant,
)