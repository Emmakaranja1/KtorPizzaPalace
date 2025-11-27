package com.emmascode.repository

import com.emmascode.models.Pizzas
import com.emmascode.dto.PizzaDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime

class PizzaRepository {
    fun findAll(): List<PizzaDTO> = transaction {
        Pizzas.selectAll().map { toDTO(it) }
    }

    fun findById(id: Int): PizzaDTO? = transaction {
        Pizzas.select { Pizzas.id eq id }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByCategory(category: String): List<PizzaDTO> = transaction {
        Pizzas.select { Pizzas.category eq category }
            .map { toDTO(it) }
    }

    fun create(
        name: String,
        description: String?,
        imageUrl: String?,
        basePrice: BigDecimal,
        category: String
    ): PizzaDTO = transaction {
        val now = LocalDateTime.now()
        val id = Pizzas.insert {
            it[Pizzas.name] = name
            it[Pizzas.description] = description
            it[Pizzas.imageUrl] = imageUrl
            it[Pizzas.basePrice] = basePrice
            it[Pizzas.category] = category
            it[createdAt] = now
            it[updatedAt] = now
        } get Pizzas.id

        findById(id)!!
    }

    fun update(
        id: Int,
        name: String?,
        description: String?,
        imageUrl: String?,
        basePrice: BigDecimal?,
        category: String?
    ): PizzaDTO? = transaction {
        val exists = Pizzas.select { Pizzas.id eq id }.count() > 0
        if (!exists) return@transaction null

        Pizzas.update({ Pizzas.id eq id }) {
            name?.let { value -> it[Pizzas.name] = value }
            description?.let { value -> it[Pizzas.description] = value }
            imageUrl?.let { value -> it[Pizzas.imageUrl] = value }
            basePrice?.let { value -> it[Pizzas.basePrice] = value }
            category?.let { value -> it[Pizzas.category] = value }
            it[updatedAt] = LocalDateTime.now()
        }

        findById(id)
    }

    fun delete(id: Int): Boolean = transaction {
        Pizzas.deleteWhere { Pizzas.id eq id } > 0
    }

    private fun toDTO(row: ResultRow): PizzaDTO = PizzaDTO(
        id = row[Pizzas.id],
        name = row[Pizzas.name],
        description = row[Pizzas.description],
        imageUrl = row[Pizzas.imageUrl],
        basePrice = row[Pizzas.basePrice],
        category = row[Pizzas.category],
        createdAt = row[Pizzas.createdAt]
    )
}
