package com.emmascode.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class RestaurantPizzaDTO(
    val id: Int,
    val restaurantId: Int,
    val pizzaId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val isAvailable: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val pizza: PizzaDTO? = null,
    val restaurant: RestaurantDTO? = null
)

@Serializable
data class CreateRestaurantPizzaDTO(
    val restaurantId: Int,
    val pizzaId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val isAvailable: Boolean = true
)

@Serializable
data class UpdateRestaurantPizzaDTO(
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal? = null,
    val isAvailable: Boolean? = null
)