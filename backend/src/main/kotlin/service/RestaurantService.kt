package com.emmascode.services

import com.emmascode.repository.RestaurantRepository
import com.emmascode.dto.*

class RestaurantService(private val repository: RestaurantRepository = RestaurantRepository()) {
    fun getAllRestaurants() = repository.findAll()
    fun getRestaurantById(id: Int) = repository.findById(id)
    fun getRestaurantsByOwnerId(ownerId: Int) = repository.findByOwnerId(ownerId)
    fun getActiveRestaurants() = repository.findActive()

    fun createRestaurant(dto: CreateRestaurantDTO): RestaurantDTO {
        return repository.create(
            dto.name, dto.address, dto.phone, dto.email,
            dto.ownerId, dto.imageUrl
        )
    }

    fun updateRestaurant(id: Int, dto: UpdateRestaurantDTO): RestaurantDTO? {
        return repository.update(
            id, dto.name, dto.address, dto.phone, dto.email,
            dto.rating, dto.imageUrl, dto.isActive
        )
    }

    fun deleteRestaurant(id: Int) = repository.delete(id)
}