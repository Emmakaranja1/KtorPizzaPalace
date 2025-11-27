package com.emmascode.dto

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class PizzaDTO(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    @Serializable(with = BigDecimalSerializer::class)
    val basePrice: BigDecimal,
    val category: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)

@Serializable
data class CreatePizzaDTO(
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val basePrice: BigDecimal,
    val category: String = "classic"
)

@Serializable
data class UpdatePizzaDTO(
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val basePrice: BigDecimal? = null,
    val category: String? = null
)


