package za.co.theookoye.domain.mother

import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.OrderStatus
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

object OrderMother {
    
    fun of(
        id: String = TestData.uuidStr(),
        version: Int = TestData.int(),
        side: OrderSide = TestData.enum(OrderSide.values()),
        quantity: BigDecimal = TestData.bigAmount(),
        filledQuantity: BigDecimal = TestData.bigAmount(),
        price: BigDecimal = TestData.bigAmount(),
        currencyPair: String = TestData.currencyCode() + TestData.currencyCode(),
        status: OrderStatus = TestData.enum(OrderStatus.values()),
        createdAt: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        updatedAt: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS),
    ) = Order(
        id = id,
        version = version,
        side = side,
        quantity = quantity,
        filledQuantity = filledQuantity,
        price = price,
        currencyPair = currencyPair,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}