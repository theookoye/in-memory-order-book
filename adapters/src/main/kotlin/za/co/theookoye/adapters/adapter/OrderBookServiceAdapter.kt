package za.co.theookoye.adapters.adapter

import com.google.inject.Inject
import io.github.oshai.kotlinlogging.KotlinLogging
import za.co.theookoye.domain.model.*
import za.co.theookoye.domain.port.OrderBookServicePort
import za.co.theookoye.domain.port.OrderRepositoryPort
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.util.*

private val log = KotlinLogging.logger { }

class OrderBookServiceAdapter @Inject constructor(
    private val orderRepository: OrderRepositoryPort,
    private val clock: Clock,
) : OrderBookServicePort {
    
    private var sequenceNumber: Long = 0
    
    override fun addOrder(order: Order) {
        orderRepository.save(order = order)
        sequenceNumber++
    }
    
    override fun getOrderBook(currencyPair: String): OrderBook {
        val bids = orderRepository.findBids()
            .filter { it.currencyPair == currencyPair }
            .map { OrderBookEntry(it.side, it.quantity, it.price, it.currencyPair, 1) }
        val asks = orderRepository.findAsks()
            .filter { it.currencyPair == currencyPair }
            .map { OrderBookEntry(it.side, it.quantity, it.price, it.currencyPair, 1) }
        
        return OrderBook(
            asks = asks,
            bids = bids,
            lastChange = Instant.now(clock),
            sequenceNumber = sequenceNumber,
        )
    }
    
    override fun matchBuyOrder(order: Order): List<Trade> = matchOrder(orderId = order.id, takerSide = OrderSide.BUY)
    
    override fun matchSellOrder(order: Order): List<Trade> = matchOrder(orderId = order.id, takerSide = OrderSide.SELL)
    
    private fun matchOrder(
        orderId: String,
        takerSide: OrderSide
    ): List<Trade> {
        val trades = mutableListOf<Trade>()
        var latestOrder = orderRepository.findRequired(id = orderId)
        var remainingQuantity = latestOrder.quantity
        
        
        while (remainingQuantity > BigDecimal.ZERO) {
            val bestOppositeOrder = when (takerSide) {
                OrderSide.BUY -> orderRepository.findAsks().minByOrNull { it.price }
                OrderSide.SELL -> orderRepository.findBids().maxByOrNull { it.price }
            } ?: break // No more orders to match
            val latestOppositeOrder = orderRepository.findRequired(id = bestOppositeOrder.id)
            
            if (latestOppositeOrder.status != OrderStatus.OPEN) {
                log.info { "Skipping match with $takerSide order with id: ${latestOppositeOrder.id}, status: ${latestOppositeOrder.status}" }
                continue
            }
            
            log.info { "Matching $takerSide order: $latestOppositeOrder with order: $latestOrder" }
            val tradeQuantity = minOf(remainingQuantity, latestOppositeOrder.quantity)
            // Execute trade and update orders
            executeTrade(
                order = latestOrder,
                oppositeOrder = latestOppositeOrder,
                tradeQuantity = tradeQuantity,
                currencyPair = latestOrder.currencyPair,
                trades = trades
            )
            remainingQuantity -= tradeQuantity
        }
        
        latestOrder = orderRepository.findRequired(id = orderId)
        if (latestOrder.status == OrderStatus.OPEN) {
            updateOrderStatus(orderId, remainingQuantity)
        }
        
        return trades
    }
    
    private fun executeTrade(
        order: Order,
        oppositeOrder: Order,
        tradeQuantity: BigDecimal,
        currencyPair: String,
        trades: MutableList<Trade>
    ) {
        val trade = Trade(
            id = UUID.randomUUID().toString(),
            version = 0,
            price = oppositeOrder.price,
            quantity = tradeQuantity,
            currencyPair = currencyPair,
            takerSide = order.side,
            sequenceId = ++sequenceNumber,
            quoteVolume = (tradeQuantity * oppositeOrder.price),
            tradedAt = Instant.now(clock),
        )
        trades.add(trade)
        
        updateOrderStatus(order.id, tradeQuantity)
        updateOrderStatus(oppositeOrder.id, tradeQuantity)
    }
    
    private fun updateOrderStatus(orderId: String, tradeQuantity: BigDecimal) {
        val order = orderRepository.findRequired(id = orderId)
        val filled = order.filledQuantity + tradeQuantity
        
        log.info { "Updating order $orderId: filled=$filled, quantity=${order.quantity}" }
        val updatedOrder = order.copy(
            filledQuantity = filled,
            status = when {
                filled == BigDecimal.ZERO -> OrderStatus.OPEN
                filled < order.quantity -> OrderStatus.PARTIALLY_FILLED
                else -> OrderStatus.FILLED
            },
            updatedAt = Instant.now(clock)
        )
        
        orderRepository.save(updatedOrder)
    }
}