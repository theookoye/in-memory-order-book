package za.co.theookoye.app

import com.google.inject.Guice
import com.google.inject.Injector
import io.github.oshai.kotlinlogging.KotlinLogging
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import za.co.theookoye.app.config.AppModule
import za.co.theookoye.app.middleware.AuthHandler
import za.co.theookoye.app.middleware.RestExceptionHandler
import za.co.theookoye.app.rest.OrderBookController
import za.co.theookoye.app.rest.OrderController
import za.co.theookoye.app.rest.TradeController

private val log = KotlinLogging.logger {}

class MainVerticle : AbstractVerticle() {
    
    override fun start(startPromise: Promise<Void>) {
        val injector = Guice.createInjector(AppModule())
        
        loadConfig { configResult ->
            if (configResult.failed()) {
                startPromise.fail("Failed to start HTTP server, could not load config")
                return@loadConfig
            }
            val config = configResult.result()
            val apiKey = config.getJsonObject("security")?.getJsonObject("api")?.getString("key")
                ?: return@loadConfig startPromise.fail("API key not set")
            val port = config.getJsonObject("server")?.getInteger("port") ?: 8080
            val router = setupRouter(injector, apiKey)
            
            vertx.createHttpServer()
                .requestHandler(router)
                .listen(port) { http ->
                    if (http.succeeded()) {
                        startPromise.complete()
                        log.info { "HTTP server started on port $port" }
                    } else {
                        startPromise.fail(http.cause())
                    }
                }
        }
    }
    
    private fun loadConfig(handler: (AsyncResult<JsonObject>) -> Unit) {
        val fileStore = ConfigStoreOptions()
            .setType("file")
            .setFormat("json")
            .setConfig(JsonObject().put("path", "application.json"))
        val retriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().addStore(fileStore))
        
        retriever.getConfig(handler)
    }
    
    private fun setupRouter(injector: Injector, apiKey: String): Router {
        val router = Router.router(vertx)
        
        router.route().handler(BodyHandler.create())
        router.route().handler(LoggerHandler.create(LoggerFormat.SHORT))
        
        router.route().handler(AuthHandler(apiKey = apiKey))
        router.route().failureHandler(RestExceptionHandler())
        
        
        injector.getInstance(TradeController::class.java).registerRoutes(router)
        injector.getInstance(OrderController::class.java).registerRoutes(router)
        injector.getInstance(OrderBookController::class.java).registerRoutes(router)
        
        
        return router
    }
}
