package za.co.theookoye.app.config

import com.google.inject.AbstractModule
import za.co.theookoye.adapters.adapter.OrderBookServiceAdapter
import za.co.theookoye.adapters.adapter.OrderRepositoryAdapter
import za.co.theookoye.adapters.adapter.TradeRepositoryAdapter
import za.co.theookoye.domain.command.handler.GetOrderBookCommandHandler
import za.co.theookoye.domain.command.handler.GetOrderCommandHandler
import za.co.theookoye.domain.command.handler.GetRecentTradesQueryHandler
import za.co.theookoye.domain.command.handler.SubmitOrderLimitCommandHandler
import za.co.theookoye.domain.port.OrderBookServicePort
import za.co.theookoye.domain.port.OrderRepositoryPort
import za.co.theookoye.domain.port.TradeRepositoryPort
import java.time.Clock

class AppModule : AbstractModule() {
    
    override fun configure() {
        bind(Clock::class.java).toInstance(Clock.systemUTC())
        bind(GetRecentTradesQueryHandler::class.java).asEagerSingleton()
        bind(GetOrderCommandHandler::class.java).asEagerSingleton()
        bind(SubmitOrderLimitCommandHandler::class.java).asEagerSingleton()
        bind(GetOrderBookCommandHandler::class.java).asEagerSingleton()
        bind(TradeRepositoryPort::class.java).to(TradeRepositoryAdapter::class.java).asEagerSingleton()
        bind(OrderBookServicePort::class.java).to(OrderBookServiceAdapter::class.java).asEagerSingleton()
        bind(OrderRepositoryPort::class.java).to(OrderRepositoryAdapter::class.java).asEagerSingleton()
    }
}