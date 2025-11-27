package com.emmascode.services

import com.emmascode.repository.PizzaRepository
import com.emmascode.dto.*

class PizzaService(private val repository: PizzaRepository = PizzaRepository()) {
    fun getAllPizzas() = repository.findAll()
    fun getPizzaById(id: Int) = repository.findById(id)
    fun getPizzasByCategory(category: String) = repository.findByCategory(category)

    fun createPizza(dto: CreatePizzaDTO): PizzaDTO {
        return repository.create(
            dto.name, dto.description, dto.imageUrl,
            dto.basePrice, dto.category
        )
    }

    fun updatePizza(id: Int, dto: UpdatePizzaDTO): PizzaDTO? {
        return repository.update(
            id, dto.name, dto.description, dto.imageUrl,
            dto.basePrice, dto.category
        )
    }

    fun deletePizza(id: Int) = repository.delete(id)
}

