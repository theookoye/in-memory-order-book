package za.co.theookoye.app.rest

import com.google.inject.Inject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import za.co.theookoye.app.mapper.extractGetRecentTradesQueryParams
import za.co.theookoye.app.mapper.toDto
import za.co.theookoye.app.mapper.toQuery
import za.co.theookoye.domain.command.handler.GetRecentTradesQueryHandler
import za.co.theookoye.dto.constant.Endpoint

class TradeController @Inject constructor(
    private val getRecentTradesQueryHandler: GetRecentTradesQueryHandler,
) {
    
    fun registerRoutes(router: Router) {
        router.get(Endpoint.GET_RECENT_TRADES).handler { ctx -> getRecentTrades(ctx) }
    }
    
    private fun getRecentTrades(ctx: RoutingContext) {
        val params = extractGetRecentTradesQueryParams(ctx.queryParams())
        val result = getRecentTradesQueryHandler.handle(query = params.toQuery())
        ctx.json(result.map { it.toDto() })
    }
}