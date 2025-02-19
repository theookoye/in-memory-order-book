package za.co.theookoye.app.mapper

import io.vertx.core.MultiMap
import za.co.theookoye.domain.command.GetRecentTradesQuery
import za.co.theookoye.domain.model.Trade
import za.co.theookoye.dto.TradeV1
import za.co.theookoye.dto.request.GetRecentTradesV1Params
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun GetRecentTradesV1Params.toQuery() = GetRecentTradesQuery(
    limit = limit,
    currencyPairs = currencyPairs,
    rangeStart = rangeStart,
    rangeEnd = rangeEnd,
)

fun Trade.toDto() = TradeV1(
    id = id,
    sequenceId = sequenceId,
    price = price.toPlainString(),
    quantity = quantity.toPlainString(),
    quoteVolume = quoteVolume.toPlainString(),
    currencyPair = currencyPair,
    takerSide = takerSide.name,
    tradedAt = tradedAt,
)

fun extractGetRecentTradesQueryParams(params: MultiMap): GetRecentTradesV1Params {
    val limit = params.get("limit")?.toIntOrNull() ?: 100
    val currencyPairs = params.get("currencyPairs")
        ?.split(",")
        ?.toSet()
        ?: setOf()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val rangeStart = params.get("rangeStart")?.let {
        try {
            LocalDate.parse(it, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
    val rangeEnd = params.get("rangeEnd")?.let {
        try {
            LocalDate.parse(it, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
    
    return GetRecentTradesV1Params(
        limit = limit,
        currencyPairs = currencyPairs,
        rangeStart = rangeStart,
        rangeEnd = rangeEnd
    )
}