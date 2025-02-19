package za.co.theookoye.domain.command.handler

import com.google.inject.Inject
import za.co.theookoye.domain.command.GetOrderCommand
import za.co.theookoye.domain.model.Order
import za.co.theookoye.domain.port.OrderRepositoryPort

class GetOrderCommandHandler @Inject constructor(
    private val repository: OrderRepositoryPort,
) {
    
    fun handle(command: GetOrderCommand): Order {
        return repository.findRequired(id = command.id)
    }
}