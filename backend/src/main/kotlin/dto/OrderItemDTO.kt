package com.emmascode.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class OrderItemDTO(
    val id: Int,
    val orderId: Int,
    val pizzaId: Int,
    val quantity: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val pricePerItem: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val subtotal: BigDecimal,
    val specialInstructions: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val pizza: PizzaDTO? = null
)

@Serializable
data class CreateOrderItemDTO(
    val pizzaId: Int,
    val quantity: Int,
    val specialInstructions: String? = null
)

@Serializable
data class UpdateOrderItemDTO(
    val quantity: Int? = null,
    val specialInstructions: String? = null
)