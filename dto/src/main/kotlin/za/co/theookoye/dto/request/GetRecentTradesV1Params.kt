package za.co.theookoye.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.valiktor.functions.isPositive
import org.valiktor.validate
import java.time.LocalDate

data class GetRecentTradesV1Params @JsonCreator constructor(
    @JsonProperty("limit") val limit: Int = 100,
    @JsonProperty("currencyPairs") val currencyPairs: Set<String> = setOf(),
    @JsonProperty("rangeStart") val rangeStart: LocalDate? = null,
    @JsonProperty("rangeEnd") val rangeEnd: LocalDate? = null,
) {
    
    init {
        validate(this) {
            validate(GetRecentTradesV1Params::limit).isPositive()
        }
    }
}
