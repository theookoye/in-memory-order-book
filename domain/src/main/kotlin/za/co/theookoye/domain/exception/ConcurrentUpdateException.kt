package za.co.theookoye.domain.exception

import kotlin.reflect.KClass

class ConcurrentUpdateException(modelClass: KClass<*>, version: Any?) :
    RuntimeException("'${modelClass.simpleName}' is outdated, the current version: $version")