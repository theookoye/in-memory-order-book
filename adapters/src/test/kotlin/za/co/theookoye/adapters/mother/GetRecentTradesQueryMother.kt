package za.co.theookoye.adapters.mother

import za.co.theookoye.domain.command.GetRecentTradesQuery
import java.time.LocalDate

object GetRecentTradesQueryMother {
    
    fun of(
        limit: Int = TestData.int().coerceIn(1, 100),
        currencyPairs: Set<String> = setOf(TestData.currencyCode() + TestData.currencyCode()),
        rangeStart: LocalDate? = null,
        rangeEnd: LocalDate? = null,
    ) = GetRecentTradesQuery(
        limit = limit,
        currencyPairs = currencyPairs,
        rangeStart = rangeStart,
        rangeEnd = rangeEnd
    )
}