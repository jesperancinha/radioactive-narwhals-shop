package org.jesperancinha.narwhals.dao

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SoldItems(
    val seaCabbage: BigDecimal,
    val tusks: Int,
)

data class EffectiveStock(
    val seaCabbage: BigDecimal,
    val tusks: Int,
)

data class CustomerOrder(
    @JsonProperty
    val customer: String,
    @JsonProperty
    val order: Order,
)

data class Order(
    @JsonProperty
    val seaCabbage: BigDecimal = BigDecimal.ZERO,
    @JsonProperty
    val tusks: Int = 0,
)

data class OrderResponse(
    @JsonProperty
    val seaCabbage: BigDecimal,
    @JsonProperty
    val tusks: Int,
    @JsonProperty("seaCabbage-available-in-days")
    val cabbagesAvailableInDays: Int = 0,
    @JsonProperty("tusks-available-in-days")
    val tusksAvailableInDays: Int = 0,
)