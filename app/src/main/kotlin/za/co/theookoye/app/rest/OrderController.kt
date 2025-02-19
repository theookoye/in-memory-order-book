package za.co.theookoye.app.rest

import com.google.inject.Inject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import za.co.theookoye.app.mapper.extractGetRecentOrdersQueryParams
import za.co.theookoye.app.mapper.toCommand
import za.co.theookoye.app.mapper.toDto
import za.co.theookoye.app.mapper.toQuery
import za.co.theookoye.domain.command.GetOrderCommand
import za.co.theookoye.domain.command.handler.GetOrderCommandHandler
import za.co.theookoye.domain.command.handler.GetRecentOrdersQueryHandler
import za.co.theookoye.domain.command.handler.SubmitOrderLimitCommandHandler
import za.co.theookoye.dto.constant.Endpoint
import za.co.theookoye.dto.request.SubmitOrderLimitV1Params

class OrderController @Inject constructor(
    private val getOrderCommandHandler: GetOrderCommandHandler,
    private val submitOrderLimitCommandHandler: SubmitOrderLimitCommandHandler,
    private val getRecentOrdersQueryHandler: GetRecentOrdersQueryHandler,
) {
    
    fun registerRoutes(router: Router) {
        router.get(Endpoint.GET_ORDER).handler { ctx -> getOrder(ctx) }
        router.post(Endpoint.SUBMIT_ORDER_LIMIT).handler { ctx -> submitOrderLimit(ctx) }
        router.get(Endpoint.GET_RECENT_ORDERS).handler { ctx -> getRecentOrders(ctx) }
    }
    
    private fun getOrder(ctx: RoutingContext) {
        val id = ctx.pathParams()["id"] as String
        val result = getOrderCommandHandler.handle(GetOrderCommand(id = id))
        ctx.json(result.toDto())
    }
    
    private fun submitOrderLimit(ctx: RoutingContext) {
        val json = ctx.body().asJsonObject()
        val params = json.mapTo(SubmitOrderLimitV1Params::class.java)
        val result = submitOrderLimitCommandHandler.handle(command = params.toCommand())
        ctx.json(result.toDto())
    }
    
    private fun getRecentOrders(ctx: RoutingContext) {
        val params = extractGetRecentOrdersQueryParams(ctx.queryParams())
        val result = getRecentOrdersQueryHandler.handle(query = params.toQuery())
        ctx.json(result.map { it.toDto() })
    }
}