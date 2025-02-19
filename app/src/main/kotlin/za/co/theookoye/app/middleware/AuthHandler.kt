package za.co.theookoye.app.middleware

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import za.co.theookoye.domain.exception.UnauthorizedAccessException

class AuthHandler(private val apiKey: String) : Handler<RoutingContext> {
    
    override fun handle(ctx: RoutingContext) {
        val apiKeyFromRequest = ctx.request().getHeader("x-api-key") ?: ctx.request().getHeader("X-API-KEY")
        
        if (apiKeyFromRequest != apiKey) {
            throw UnauthorizedAccessException("Invalid API key")
        }
        
        ctx.next()
    }
}