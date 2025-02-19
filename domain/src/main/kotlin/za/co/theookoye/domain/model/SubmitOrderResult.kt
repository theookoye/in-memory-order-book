package za.co.theookoye.domain.model

data class SubmitOrderResult(
    val order: Order,
    val trades: List<Trade>,
)
