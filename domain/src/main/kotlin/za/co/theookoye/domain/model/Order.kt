package za.co.theookoye.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val version: Int = 0,
    val side: OrderSide,
    val quantity: BigDecimal,
    val filledQuantity: BigDecimal = BigDecimal.ZERO,
    val price: BigDecimal,
    val currencyPair: String,
    val status: OrderStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class OrderSide {
    BUY, SELL
}

enum class OrderStatus {
    OPEN, FILLED, PARTIALLY_FILLED
}