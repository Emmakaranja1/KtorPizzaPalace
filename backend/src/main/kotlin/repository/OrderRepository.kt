package com.emmascode.repository

import com.emmascode.models.OrderItems
import com.emmascode.dto.OrderItemDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderItemRepository {
    fun findAll(): List<OrderItemDTO> = transaction {
        OrderItems.selectAll().map { toDTO(it) }
    }

    fun findById(id: Int): OrderItemDTO? = transaction {
        OrderItems.select { OrderItems.id eq id }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByOrderId(orderId: Int): List<OrderItemDTO> = transaction {
        OrderItems.select { OrderItems.orderId eq orderId }
            .map { toDTO(it) }
    }

    fun create(
        orderId: Int, pizzaId: Int, quantity: Int,
        pricePerItem: BigDecimal, specialInstructions: String?
    ): OrderItemDTO = transaction {
        val subtotal = pricePerItem * quantity.toBigDecimal()
        val now = LocalDateTime.now()

        val id = OrderItems.insert {
            it[OrderItems.orderId] = orderId
            it[OrderItems.pizzaId] = pizzaId
            it[OrderItems.quantity] = quantity
            it[OrderItems.pricePerItem] = pricePerItem
            it[OrderItems.subtotal] = subtotal
            it[OrderItems.specialInstructions] = specialInstructions
            it[createdAt] = now
        } get OrderItems.id

        findById(id)!!
    }

    fun update(
        id: Int, quantity: Int?, specialInstructions: String?
    ): OrderItemDTO? = transaction {
        val item = findById(id) ?: return@transaction null

        OrderItems.update({ OrderItems.id eq id }) {
            quantity?.let { q ->
                it[OrderItems.quantity] = q
                it[subtotal] = item.pricePerItem * q.toBigDecimal()
            }
            specialInstructions?.let { v -> it[OrderItems.specialInstructions] = v }
        }
        findById(id)
    }

    fun delete(id: Int): Boolean = transaction {
        OrderItems.deleteWhere { OrderItems.id eq id } > 0
    }

    private fun toDTO(row: ResultRow) = OrderItemDTO(
        id = row[OrderItems.id],
        orderId = row[OrderItems.orderId],
        pizzaId = row[OrderItems.pizzaId],
        quantity = row[OrderItems.quantity],
        pricePerItem = row[OrderItems.pricePerItem],
        subtotal = row[OrderItems.subtotal],
        specialInstructions = row[OrderItems.specialInstructions],
        createdAt = row[OrderItems.createdAt]
    )
}