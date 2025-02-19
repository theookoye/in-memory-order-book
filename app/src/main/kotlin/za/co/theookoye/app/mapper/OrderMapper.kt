package za.co.theookoye.app.mapper

import io.vertx.core.MultiMap
import za.co.theookoye.domain.command.GetRecentOrdersQuery
import za.co.theookoye.domain.command.SubmitOrderLimitCommand
import za.co.theookoye.domain.exception.BadRequestCode
import za.co.theookoye.domain.exception.BadRequestException
import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.SubmitOrderResult
import za.co.theookoye.dto.OrderV1
import za.co.theookoye.dto.request.GetRecentOrdersV1Params
import za.co.theookoye.dto.request.SubmitOrderLimitV1Params
import za.co.theookoye.dto.response.SubmitOrderLimitV1Response
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun SubmitOrderLimitV1Params.toCommand() = SubmitOrderLimitCommand(
    side = orderSideOf(side),
    quantity = bigDecimalOf(quantity),
    price = bigDecimalOf(price),
    currencyPair = currencyPair,
)

fun SubmitOrderResult.toDto() = SubmitOrderLimitV1Response(
    order = order.toDto(),
    trades = trades.map { it.toDto() },
)

fun Order.toDto() = OrderV1(
    id = id,
    side = side.name,
    quantity = quantity.toPlainString(),
    price = price.toPlainString(),
    currencyPair = currencyPair,
    status = status.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun extractGetRecentOrdersQueryParams(params: MultiMap): GetRecentOrdersV1Params {
    val limit = params.get("limit")?.toIntOrNull() ?: 100
    val currencyPairs = params.get("currencyPairs")
        ?.split(",")
        ?.toSet()
        ?: setOf()
    val sides = params.get("sides")
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
    
    return GetRecentOrdersV1Params(
        limit = limit,
        currencyPairs = currencyPairs,
        sides = sides,
        rangeStart = rangeStart,
        rangeEnd = rangeEnd
    )
}

fun GetRecentOrdersV1Params.toQuery() = GetRecentOrdersQuery(
    limit = limit,
    currencyPairs = currencyPairs,
    sides = sides.map { orderSideOf(it) }.toSet(),
    rangeStart = rangeStart,
    rangeEnd = rangeEnd,
)

private fun orderSideOf(orderSide: String): OrderSide = try {
    OrderSide.valueOf(orderSide)
} catch (e: IllegalArgumentException) {
    throw BadRequestException(BadRequestCode.INCORRECT_ORDER_SIDE, "Unknown order side: $orderSide")
}

private fun bigDecimalOf(value: String): BigDecimal =
    try {
        BigDecimal(value)
    } catch (e: NumberFormatException) {
        throw BadRequestException(BadRequestCode.INVALID_VALUE, "Invalid value: $value")
    }