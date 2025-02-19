package za.co.theookoye.domain.command

import za.co.theookoye.domain.model.OrderSide
import java.time.LocalDate

data class GetRecentOrdersQuery(
    val limit: Int,
    val currencyPairs: Set<String>,
    val sides: Set<OrderSide>,
    val rangeStart: LocalDate?,
    val rangeEnd: LocalDate?
)
