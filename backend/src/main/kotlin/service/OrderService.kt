package com.emmascode.services

import com.emmascode.repository.OrderRepository
import com.emmascode.repository.OrderItemRepository
import com.emmascode.repository.RestaurantPizzaRepository
import com.emmascode.repository.PizzaRepository
import com.emmascode.dto.*
import java.math.BigDecimal

class OrderService(
    private val orderRepository: OrderRepository = OrderRepository(),
    private val orderItemRepository: OrderItemRepository = OrderItemRepository(),
    private val restaurantPizzaRepository: RestaurantPizzaRepository = RestaurantPizzaRepository(),
    private val pizzaRepository: PizzaRepository = PizzaRepository()
) {
    fun getAllOrders() = orderRepository.findAll()
    fun getOrderById(id: Int) = orderRepository.findById(id)
    fun getOrdersByUserId(userId: Int) = orderRepository.findByUserId(userId)
    fun getOrdersByRestaurantId(restaurantId: Int) = orderRepository.findByRestaurantId(restaurantId)
    fun getOrdersByStatus(status: String) = orderRepository.findByStatus(status)

    fun createOrder(dto: CreateOrderDTO): OrderDTO {
        var totalAmount = BigDecimal.ZERO

        // Validate all pizzas exist and calculate total with fallback logic
        dto.items.forEach { item ->
            // Get the pizza to ensure it exists
            val pizza = pizzaRepository.findById(item.pizzaId)
                ?: throw IllegalArgumentException("Pizza ${item.pizzaId} does not exist")

            // Try to get restaurant-specific pricing
            val restaurantPizza = restaurantPizzaRepository.findByRestaurantAndPizza(
                dto.restaurantId,
                item.pizzaId
            )

            // Fallback logic: Use restaurant price if available, otherwise use base price
            val priceToUse = restaurantPizza?.price ?: pizza.basePrice

            // Check availability if listed under restaurant
            if (restaurantPizza != null && !restaurantPizza.isAvailable) {
                throw IllegalArgumentException(
                    "Pizza '${pizza.name}' is currently unavailable at this restaurant"
                )
            }

            val itemTotal = priceToUse * item.quantity.toBigDecimal()
            totalAmount += itemTotal
        }

        // Create order
        val order = orderRepository.create(
            dto.userId,
            dto.restaurantId,
            totalAmount,
            dto.deliveryAddress,
            dto.paymentMethod,
            dto.notes
        )

        // Create order items with correct pricing
        dto.items.forEach { item ->
            val pizza = pizzaRepository.findById(item.pizzaId)!!

            // Apply same fallback logic for order items
            val restaurantPizza = restaurantPizzaRepository.findByRestaurantAndPizza(
                dto.restaurantId,
                item.pizzaId
            )
            val priceToUse = restaurantPizza?.price ?: pizza.basePrice

            orderItemRepository.create(
                order.id,
                item.pizzaId,
                item.quantity,
                priceToUse,
                item.specialInstructions
            )
        }

        return order
    }

    fun updateOrder(id: Int, dto: UpdateOrderDTO): OrderDTO? {
        return orderRepository.update(
            id, dto.status, dto.paymentStatus, dto.deliveryAddress, dto.notes
        )
    }

    fun deleteOrder(id: Int) = orderRepository.delete(id)

    /**
     * Get the price for a pizza at a specific restaurant
     * Falls back to base price if not listed
     */
    fun getPizzaPriceAtRestaurant(restaurantId: Int, pizzaId: Int): BigDecimal {
        val pizza = pizzaRepository.findById(pizzaId)
            ?: throw IllegalArgumentException("Pizza not found")

        val restaurantPizza = restaurantPizzaRepository.findByRestaurantAndPizza(
            restaurantId,
            pizzaId
        )

        return restaurantPizza?.price ?: pizza.basePrice
    }

    /**
     * Check if a pizza is available at a restaurant
     * Returns true if not listed (using base price) or if listed and available
     */
    fun isPizzaAvailableAtRestaurant(restaurantId: Int, pizzaId: Int): Boolean {
        val pizza = pizzaRepository.findById(pizzaId) ?: return false

        val restaurantPizza = restaurantPizzaRepository.findByRestaurantAndPizza(
            restaurantId,
            pizzaId
        )

        // If not listed, it's available (will use base price)
        // If listed, check availability flag
        return restaurantPizza?.isAvailable ?: true
    }
}

