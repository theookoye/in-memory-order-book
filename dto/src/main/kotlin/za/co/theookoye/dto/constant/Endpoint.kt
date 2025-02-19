package za.co.theookoye.dto.constant

object Endpoint {
    
    const val GET_RECENT_ORDERS = "/v1/orders"
    const val GET_ORDER = "/v1/orders/:id"
    const val SUBMIT_ORDER_LIMIT = "/v1/orders/limit"
    const val GET_RECENT_TRADES = "/v1/trades"
    const val GET_ORDER_BOOK = "/v1/:currencyPair/order-book"
}