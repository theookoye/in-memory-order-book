package za.co.theookoye.domain.mother

import za.co.theookoye.domain.command.SubmitOrderLimitCommand
import za.co.theookoye.domain.model.OrderSide
import java.math.BigDecimal

object SubmitOrderLimitCommandMother {
    
    fun of(
        side: OrderSide = TestData.enum(OrderSide.values()),
        quantity: BigDecimal = TestData.bigAmount(),
        price: BigDecimal = TestData.bigAmount(),
        currencyPair: String = TestData.currencyCode() + TestData.currencyCode(),
    ) = SubmitOrderLimitCommand(
        side = side,
        quantity = quantity,
        price = price,
        currencyPair = currencyPair
    )
}