package com.emmascode.models


import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val createdAt: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String = "customer"
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)

@Serializable
data class Pizza(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val basePrice: String,
    val category: String,
    val createdAt: String
)

@Serializable
data class CreatePizzaRequest(
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val basePrice: String,
    val category: String = "classic"
)

@Serializable
data class Restaurant(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String?,
    val email: String?,
    val ownerId: Int,
    val rating: String,
    val imageUrl: String?,
    val isActive: Boolean,
    val createdAt: String
)

@Serializable
data class CreateRestaurantRequest(
    val name: String,
    val address: String,
    val phone: String? = null,
    val email: String? = null,
    val ownerId: Int,
    val imageUrl: String? = null
)

@Serializable
data class Order(
    val id: Int,
    val userId: Int,
    val restaurantId: Int,
    val status: String,
    val totalAmount: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val notes: String?,
    val createdAt: String
)

@Serializable
data class OrderItem(
    val pizzaId: Int,
    val quantity: Int,
    val specialInstructions: String? = null
)

@Serializable
data class CreateOrderRequest(
    val userId: Int,
    val restaurantId: Int,
    val deliveryAddress: String,
    val paymentMethod: String = "cash",
    val notes: String? = null,
    val items: List<OrderItem>
)

@Serializable
data class PizzaPrice(
    val price: String,
    val isAvailable: Boolean,
    val isCustomPrice: Boolean
)