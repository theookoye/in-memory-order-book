package za.co.theookoye.app.rest

import com.google.inject.Inject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import za.co.theookoye.app.mapper.toDto
import za.co.theookoye.domain.command.GetOrderBookCommand
import za.co.theookoye.domain.command.handler.GetOrderBookCommandHandler
import za.co.theookoye.dto.constant.Endpoint

class OrderBookController @Inject constructor(
    private val getOrderBookCommandHandler: GetOrderBookCommandHandler,
) {
    
    fun registerRoutes(router: Router) {
        router.get(Endpoint.GET_ORDER_BOOK).handler { ctx -> getOrderBook(ctx) }
    }
    
    private fun getOrderBook(ctx: RoutingContext) {
        val currencyPair = ctx.pathParams()["currencyPair"] as String
        val result = getOrderBookCommandHandler.handle(command = GetOrderBookCommand(currencyPair = currencyPair))
        ctx.json(result.toDto())
    }
}