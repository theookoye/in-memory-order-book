package za.co.theookoye.dto

import java.time.Instant

data class OrderV1(
    val id: String,
    val side: String,
    val quantity: String,
    val price: String,
    val currencyPair: String,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
