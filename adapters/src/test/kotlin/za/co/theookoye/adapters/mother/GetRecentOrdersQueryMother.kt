package za.co.theookoye.adapters.mother

import za.co.theookoye.domain.command.GetRecentOrdersQuery
import za.co.theookoye.domain.model.OrderSide
import java.time.LocalDate

object GetRecentOrdersQueryMother {
    
    fun of(
        limit: Int = TestData.int(),
        currencyPairs: Set<String> = setOf(TestData.currencyCode() + TestData.currencyCode()),
        sides: Set<OrderSide> = setOf(TestData.enum(OrderSide.values())),
        rangeStart: LocalDate? = null,
        rangeEnd: LocalDate? = null,
    ) = GetRecentOrdersQuery(
        limit = limit,
        currencyPairs = currencyPairs,
        sides = sides,
        rangeStart = rangeStart,
        rangeEnd = rangeEnd
    )
}