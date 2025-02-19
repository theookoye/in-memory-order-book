package za.co.theookoye.domain.command

import java.time.LocalDate

data class GetRecentTradesQuery(
    val limit: Int,
    val currencyPairs: Set<String>,
    val rangeStart: LocalDate?,
    val rangeEnd: LocalDate?
)
