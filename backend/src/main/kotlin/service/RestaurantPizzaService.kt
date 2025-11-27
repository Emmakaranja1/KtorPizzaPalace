package com.emmascode.services

import com.emmascode.repository.RestaurantPizzaRepository
import com.emmascode.dto.*

class RestaurantPizzaService(private val repository: RestaurantPizzaRepository = RestaurantPizzaRepository()) {
    fun getAllRestaurantPizzas() = repository.findAll()
    fun getRestaurantPizzaById(id: Int) = repository.findById(id)
    fun getRestaurantPizzasByRestaurantId(restaurantId: Int) = repository.findByRestaurantId(restaurantId)
    fun getRestaurantPizzasByPizzaId(pizzaId: Int) = repository.findByPizzaId(pizzaId)

    fun createRestaurantPizza(dto: CreateRestaurantPizzaDTO): RestaurantPizzaDTO {
        return repository.create(
            dto.restaurantId, dto.pizzaId, dto.price, dto.isAvailable
        )
    }

    fun updateRestaurantPizza(id: Int, dto: UpdateRestaurantPizzaDTO): RestaurantPizzaDTO? {
        return repository.update(id, dto.price, dto.isAvailable)
    }

    fun deleteRestaurantPizza(id: Int) = repository.delete(id)
}
