package za.co.theookoye.domain.mother

import za.co.theookoye.domain.model.OrderSide
import za.co.theookoye.domain.model.Trade
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

object TradeMother {
    
    fun of(
        id: String = TestData.uuidStr(),
        sequenceId: Long = TestData.long(),
        version: Int = TestData.int(),
        price: BigDecimal = TestData.bigAmount(),
        quantity: BigDecimal = TestData.bigAmount(),
        quoteVolume: BigDecimal = TestData.bigAmount(),
        currencyPair: String = TestData.currencyCode() + TestData.currencyCode(),
        takerSide: OrderSide = TestData.enum(OrderSide.values()),
        tradedAt: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    ) = Trade(
        id = id,
        sequenceId = sequenceId,
        version = version,
        price = price,
        quantity = quantity,
        quoteVolume = quoteVolume,
        currencyPair = currencyPair,
        takerSide = takerSide,
        tradedAt = tradedAt
    )
}