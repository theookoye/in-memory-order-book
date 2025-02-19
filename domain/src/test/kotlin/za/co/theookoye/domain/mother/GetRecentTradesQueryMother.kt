package za.co.theookoye.domain.mother

import za.co.theookoye.domain.command.GetRecentTradesQuery
import java.time.LocalDate

object GetRecentTradesQueryMother {
    
    fun of(
        limit: Int = TestData.int(),
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