package za.co.theookoye.domain.mother

import za.co.theookoye.domain.model.OrderBookEntry
import za.co.theookoye.domain.model.OrderSide
import java.math.BigDecimal

object OrderBookEntryMother {
    
    fun of(
        side: OrderSide = TestData.enum(OrderSide.values()),
        quantity: BigDecimal = TestData.bigAmount(),
        price: BigDecimal = TestData.bigAmount(),
        currencyPair: String = TestData.currencyCode() + TestData.currencyCode(),
        orderCount: Int = TestData.int()
    ) = OrderBookEntry(
        side = side,
        quantity = quantity,
        price = price,
        currencyPair = currencyPair,
        orderCount = orderCount
    )
}