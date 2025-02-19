package za.co.theookoye.app.mapper

import za.co.theookoye.domain.model.OrderBook
import za.co.theookoye.domain.model.OrderBookEntry
import za.co.theookoye.dto.OrderBookEntryV1
import za.co.theookoye.dto.OrderBookV1

fun OrderBook.toDto() = OrderBookV1(
    asks = asks.map { it.toDto() },
    bids = bids.map { it.toDto() },
    lastChange = lastChange,
    sequenceNumber = sequenceNumber,
)

fun OrderBookEntry.toDto() = OrderBookEntryV1(
    side = side.name,
    quantity = quantity.toPlainString(),
    price = price.toPlainString(),
    currencyPair = currencyPair,
    orderCount = orderCount
)