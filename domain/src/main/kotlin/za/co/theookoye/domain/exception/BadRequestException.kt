package za.co.theookoye.domain.exception

class BadRequestException(val code: String, override val message: String) : RuntimeException(message)

object BadRequestCode {
    
    const val INCORRECT_ORDER_SIDE = "IncorrectOrderSide"
    const val INVALID_VALUE = "InvalidValue"
}