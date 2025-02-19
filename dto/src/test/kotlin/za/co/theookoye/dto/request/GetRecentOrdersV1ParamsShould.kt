package za.co.theookoye.dto.request

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.valiktor.ConstraintViolationException

internal class GetRecentOrdersV1ParamsShould {
    
    @Test
    fun `pass validation when all fields are valid`() {
        // given - when - then
        shouldNotThrow<Exception> {
            GetRecentOrdersV1Params()
        }
    }
    
    @ParameterizedTest
    @ValueSource(ints = [0, -1])
    fun `throw validation exception when limit is not valid`(value: Int) {
        // given - when - then
        shouldThrow<ConstraintViolationException> {
            GetRecentOrdersV1Params(limit = value)
        }
    }
}