package za.co.theookoye.dto.response

import za.co.theookoye.dto.OrderV1
import za.co.theookoye.dto.TradeV1

data class SubmitOrderLimitV1Response(
    val trades: List<TradeV1>,
    val order: OrderV1,
)
