package com.emmascode.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class RestaurantDTO(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String?,
    val email: String?,
    val ownerId: Int,
    @Serializable(with = BigDecimalSerializer::class)
    val rating: BigDecimal,
    val imageUrl: String?,
    val isActive: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)

@Serializable
data class CreateRestaurantDTO(
    val name: String,
    val address: String,
    val phone: String? = null,
    val email: String? = null,
    val ownerId: Int,
    val imageUrl: String? = null
)

@Serializable
data class UpdateRestaurantDTO(
    val name: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val rating: BigDecimal? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null
)