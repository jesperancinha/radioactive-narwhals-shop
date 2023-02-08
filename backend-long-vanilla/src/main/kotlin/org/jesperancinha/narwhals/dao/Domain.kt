package org.jesperancinha.narwhals.dao

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SoldItems(
    val seaCabbage: Long,
    val tusks: Long,
) {
    companion object {
        operator fun invoke() = SoldItems(0, 0)
    }
}

data class EffectiveStock(
    val seaCabbage: Long,
    val tusks: Long,
)

data class CustomerOrder(
    @JsonProperty
    val customer: String,
    @JsonProperty
    val order: Order,
)

data class Order(
    @JsonProperty
    val seaCabbage: Long = 0,
    @JsonProperty
    val tusks: Long = 0,
)

data class OrderResponse(
    @JsonProperty
    val seaCabbage: BigDecimal,
    @JsonProperty
    val tusks: Long,
    @JsonProperty("seaCabbage-available-in-days")
    val cabbagesAvailableInDays: Int = 0,
    @JsonProperty("tusks-available-in-days")
    val tusksAvailableInDays: Int = 0,
)