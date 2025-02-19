package za.co.theookoye.adapters.mother

import za.co.theookoye.domain.model.OrderBook
import za.co.theookoye.domain.model.OrderBookEntry
import java.time.Instant
import java.time.temporal.ChronoUnit

object OrderBookMother {
    
    fun of(
        asks: List<OrderBookEntry> = listOf(OrderBookEntryMother.of()),
        bids: List<OrderBookEntry> = listOf(OrderBookEntryMother.of()),
        lastChange: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS),
        sequenceNumber: Long = TestData.long()
    ) = OrderBook(
        asks = asks,
        bids = bids,
        lastChange = lastChange,
        sequenceNumber = sequenceNumber
    )
}