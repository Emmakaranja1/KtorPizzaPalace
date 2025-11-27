package com.emmascode.dto

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)

@Serializable
data class CreateUserDTO(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "customer"
)

@Serializable
data class UpdateUserDTO(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null
)

@Serializable
data class LoginDTO(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDTO(
    val token: String,
    val user: UserDTO
)


