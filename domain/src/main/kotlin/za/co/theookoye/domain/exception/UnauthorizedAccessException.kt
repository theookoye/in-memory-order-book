package za.co.theookoye.domain.exception

class UnauthorizedAccessException(override val message: String) :
    RuntimeException(message)