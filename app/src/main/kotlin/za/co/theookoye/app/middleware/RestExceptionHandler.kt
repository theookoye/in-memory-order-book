package za.co.theookoye.app.middleware

import io.github.oshai.kotlinlogging.KotlinLogging
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.valiktor.ConstraintViolationException
import org.valiktor.i18n.mapToMessage
import za.co.theookoye.domain.exception.BadRequestException
import za.co.theookoye.domain.exception.ModelNotFoundException
import za.co.theookoye.domain.exception.TradeOrderException
import za.co.theookoye.domain.exception.UnauthorizedAccessException
import za.co.theookoye.dto.constant.ErrorCode

private val log = KotlinLogging.logger {}

class RestExceptionHandler : Handler<RoutingContext> {
    
    override fun handle(ctx: RoutingContext) {
        val failure = ctx.failure()
        
        log.error(failure) { "Exception caught in RestExceptionHandler" }
        
        val (httpStatus, errorCode) = when (failure) {
            is ModelNotFoundException -> 404 to ErrorCode.NOT_FOUND
            is BadRequestException, is TradeOrderException -> 400 to ErrorCode.BAD_REQUEST
            is ConstraintViolationException -> 422 to ErrorCode.INVALID_REQUEST
            is UnauthorizedAccessException -> 401 to ErrorCode.UNAUTHORIZED
            else -> 500 to ErrorCode.INTERNAL_SERVER_ERROR
        }
        val errorResponse = createError(code = errorCode, exception = failure).toString()
        
        ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(httpStatus)
            .end(errorResponse)
    }
    
    private fun createError(code: String, exception: Throwable?): JsonObject {
        val message = when (exception) {
            is ConstraintViolationException -> {
                exception.constraintViolations
                    .mapToMessage("messages")
                    .joinToString(", ") { "${it.property}: ${it.message}" }
            }
            
            else -> (exception?.message ?: "Unknown message")
        }
        
        return json {
            obj("code" to code, "message" to message)
        }
    }
}
