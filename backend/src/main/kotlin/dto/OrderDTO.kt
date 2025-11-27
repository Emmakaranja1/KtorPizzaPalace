package com.emmascode.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class OrderDTO(
    val id: Int,
    val userId: Int,
    val restaurantId: Int,
    val status: String,
    @Serializable(with = BigDecimalSerializer::class)
    val totalAmount: BigDecimal,
    val deliveryAddress: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val notes: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val items: List<OrderItemDTO>? = null,
    val restaurant: RestaurantDTO? = null,
    val user: UserDTO? = null
)

@Serializable
data class CreateOrderDTO(
    val userId: Int,
    val restaurantId: Int,
    val deliveryAddress: String,
    val paymentMethod: String = "cash",
    val notes: String? = null,
    val items: List<CreateOrderItemDTO>
)

@Serializable
data class UpdateOrderDTO(
    val status: String? = null,
    val paymentStatus: String? = null,
    val deliveryAddress: String? = null,
    val notes: String? = null
)