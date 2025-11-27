package com.emmascode.repository

import com.emmascode.models.RestaurantPizzas
import com.emmascode.dto.RestaurantPizzaDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime

class RestaurantPizzaRepository {
    fun findAll(): List<RestaurantPizzaDTO> = transaction {
        RestaurantPizzas.selectAll().map { toDTO(it) }
    }

    fun findById(id: Int): RestaurantPizzaDTO? = transaction {
        RestaurantPizzas.select { RestaurantPizzas.id eq id }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByRestaurantId(restaurantId: Int): List<RestaurantPizzaDTO> = transaction {
        RestaurantPizzas.select { RestaurantPizzas.restaurantId eq restaurantId }
            .map { toDTO(it) }
    }

    fun findByPizzaId(pizzaId: Int): List<RestaurantPizzaDTO> = transaction {
        RestaurantPizzas.select { RestaurantPizzas.pizzaId eq pizzaId }
            .map { toDTO(it) }
    }

    // NEW: Find specific restaurant-pizza combination
    fun findByRestaurantAndPizza(restaurantId: Int, pizzaId: Int): RestaurantPizzaDTO? = transaction {
        RestaurantPizzas.select {
            (RestaurantPizzas.restaurantId eq restaurantId) and
                    (RestaurantPizzas.pizzaId eq pizzaId)
        }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun create(restaurantId: Int, pizzaId: Int, price: BigDecimal, isAvailable: Boolean): RestaurantPizzaDTO = transaction {
        val now = LocalDateTime.now()
        val id = RestaurantPizzas.insert {
            it[RestaurantPizzas.restaurantId] = restaurantId
            it[RestaurantPizzas.pizzaId] = pizzaId
            it[RestaurantPizzas.price] = price
            it[RestaurantPizzas.isAvailable] = isAvailable
            it[createdAt] = now
            it[updatedAt] = now
        } get RestaurantPizzas.id
        findById(id)!!
    }

    fun update(id: Int, price: BigDecimal?, isAvailable: Boolean?): RestaurantPizzaDTO? = transaction {
        val exists = RestaurantPizzas.select { RestaurantPizzas.id eq id }.count() > 0
        if (!exists) return@transaction null

        RestaurantPizzas.update({ RestaurantPizzas.id eq id }) {
            price?.let { v -> it[RestaurantPizzas.price] = v }
            isAvailable?.let { v -> it[RestaurantPizzas.isAvailable] = v }
            it[updatedAt] = LocalDateTime.now()
        }
        findById(id)
    }

    fun delete(id: Int): Boolean = transaction {
        RestaurantPizzas.deleteWhere { RestaurantPizzas.id eq id } > 0
    }

    private fun toDTO(row: ResultRow) = RestaurantPizzaDTO(
        id = row[RestaurantPizzas.id],
        restaurantId = row[RestaurantPizzas.restaurantId],
        pizzaId = row[RestaurantPizzas.pizzaId],
        price = row[RestaurantPizzas.price],
        isAvailable = row[RestaurantPizzas.isAvailable],
        createdAt = row[RestaurantPizzas.createdAt]
    )
}
