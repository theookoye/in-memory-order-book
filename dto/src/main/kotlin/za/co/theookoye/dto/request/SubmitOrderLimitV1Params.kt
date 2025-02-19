package za.co.theookoye.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

data class SubmitOrderLimitV1Params @JsonCreator constructor(
    @JsonProperty("side") val side: String,
    @JsonProperty("quantity") val quantity: String,
    @JsonProperty("price") val price: String,
    @JsonProperty("currencyPair") val currencyPair: String,
) {
    
    init {
        validate(this) {
            validate(SubmitOrderLimitV1Params::quantity).isNotBlank()
            validate(SubmitOrderLimitV1Params::price).isNotBlank()
        }
    }
}

