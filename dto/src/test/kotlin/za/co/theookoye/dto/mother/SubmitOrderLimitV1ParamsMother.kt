package za.co.theookoye.dto.mother

import za.co.theookoye.dto.request.SubmitOrderLimitV1Params

object SubmitOrderLimitV1ParamsMother {
    
    fun of(
        side: String = TestData.string(),
        quantity: String = TestData.bigAmount().toPlainString(),
        price: String = TestData.bigAmount().toPlainString(),
        currencyPair: String = TestData.currencyCode() + TestData.currencyCode(),
    ) = SubmitOrderLimitV1Params(
        side = side,
        quantity = quantity,
        price = price,
        currencyPair = currencyPair,
    )
}