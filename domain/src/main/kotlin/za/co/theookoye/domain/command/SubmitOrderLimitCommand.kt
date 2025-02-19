package za.co.theookoye.domain.command

import za.co.theookoye.domain.model.OrderSide
import java.math.BigDecimal

data class SubmitOrderLimitCommand(
    val side: OrderSide,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: String,
)
