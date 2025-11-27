package com.emmascode.repository

import com.emmascode.models.Restaurants
import com.emmascode.dto.RestaurantDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime

class RestaurantRepository {
    fun findAll(): List<RestaurantDTO> = transaction {
        Restaurants.selectAll().map { toDTO(it) }
    }

    fun findById(id: Int): RestaurantDTO? = transaction {
        Restaurants.select { Restaurants.id eq id }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByOwnerId(ownerId: Int): List<RestaurantDTO> = transaction {
        Restaurants.select { Restaurants.ownerId eq ownerId }
            .map { toDTO(it) }
    }

    fun findActive(): List<RestaurantDTO> = transaction {
        Restaurants.select { Restaurants.isActive eq true }
            .map { toDTO(it) }
    }

    fun create(
        name: String, address: String, phone: String?, email: String?,
        ownerId: Int, imageUrl: String?
    ): RestaurantDTO = transaction {
        val now = LocalDateTime.now()
        val id = Restaurants.insert {
            it[Restaurants.name] = name
            it[Restaurants.address] = address
            it[Restaurants.phone] = phone
            it[Restaurants.email] = email
            it[Restaurants.ownerId] = ownerId
            it[Restaurants.imageUrl] = imageUrl
            it[createdAt] = now
            it[updatedAt] = now
        } get Restaurants.id
        findById(id)!!
    }

    fun update(
        id: Int, name: String?, address: String?, phone: String?, email: String?,
        rating: BigDecimal?, imageUrl: String?, isActive: Boolean?
    ): RestaurantDTO? = transaction {
        val exists = Restaurants.select { Restaurants.id eq id }.count() > 0
        if (!exists) return@transaction null

        Restaurants.update({ Restaurants.id eq id }) {
            name?.let { v -> it[Restaurants.name] = v }
            address?.let { v -> it[Restaurants.address] = v }
            phone?.let { v -> it[Restaurants.phone] = v }
            email?.let { v -> it[Restaurants.email] = v }
            rating?.let { v -> it[Restaurants.rating] = v }
            imageUrl?.let { v -> it[Restaurants.imageUrl] = v }
            isActive?.let { v -> it[Restaurants.isActive] = v }
            it[updatedAt] = LocalDateTime.now()
        }
        findById(id)
    }

    fun delete(id: Int): Boolean = transaction {
        Restaurants.deleteWhere { Restaurants.id eq id } > 0
    }

    private fun toDTO(row: ResultRow) = RestaurantDTO(
        id = row[Restaurants.id],
        name = row[Restaurants.name],
        address = row[Restaurants.address],
        phone = row[Restaurants.phone],
        email = row[Restaurants.email],
        ownerId = row[Restaurants.ownerId],
        rating = row[Restaurants.rating],
        imageUrl = row[Restaurants.imageUrl],
        isActive = row[Restaurants.isActive],
        createdAt = row[Restaurants.createdAt]
    )
}