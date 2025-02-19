package za.co.theookoye.dto.request

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import org.valiktor.ConstraintViolationException
import za.co.theookoye.dto.mother.SubmitOrderLimitV1ParamsMother

internal class SubmitOrderLimitV1ParamsShould {
    
    @Test
    fun `pass validation when all fields are valid`() {
        // given - when - then
        shouldNotThrow<Exception> {
            SubmitOrderLimitV1ParamsMother.of()
        }
    }
    
    @Test
    fun `throw validation exception when quantity is blank`() {
        // given - when - then
        shouldThrow<ConstraintViolationException> {
            SubmitOrderLimitV1ParamsMother.of(quantity = "")
        }
    }
    
    @Test
    fun `throw validation exception when price is blank`() {
        // given - when - then
        shouldThrow<ConstraintViolationException> {
            SubmitOrderLimitV1ParamsMother.of(price = "")
        }
    }
}