package com.emmascode.repository

import com.emmascode.models.Orders
import com.emmascode.dto.OrderDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class OrderRepository {
    fun findAll(): List<OrderDTO> = transaction {
        Orders.selectAll().map { toDTO(it) }
    }

    fun findById(id: Int): OrderDTO? = transaction {
        Orders.select { Orders.id eq id }
            .mapNotNull { toDTO(it) }
            .singleOrNull()
    }

    fun findByUserId(userId: Int): List<OrderDTO> = transaction {
        Orders.select { Orders.userId eq userId }
            .map { toDTO(it) }
    }

    fun findByRestaurantId(restaurantId: Int): List<OrderDTO> = transaction {
        Orders.select { Orders.restaurantId eq restaurantId }
            .map { toDTO(it) }
    }

    fun findByStatus(status: String): List<OrderDTO> = transaction {
        Orders.select { Orders.status eq status }
            .map { toDTO(it) }
    }

    fun create(
        userId: Int, restaurantId: Int, totalAmount: BigDecimal,
        deliveryAddress: String, paymentMethod: String, notes: String?
    ): OrderDTO = transaction {
        val now = LocalDateTime.now()
        val id = Orders.insert {
            it[Orders.userId] = userId
            it[Orders.restaurantId] = restaurantId
            it[Orders.totalAmount] = totalAmount
            it[Orders.deliveryAddress] = deliveryAddress
            it[Orders.paymentMethod] = paymentMethod
            it[Orders.notes] = notes
            it[createdAt] = now
            it[updatedAt] = now
        } get Orders.id
        findById(id)!!
    }

    fun update(
        id: Int, status: String?, paymentStatus: String?,
        deliveryAddress: String?, notes: String?
    ): OrderDTO? = transaction {
        val exists = Orders.select { Orders.id eq id }.count() > 0
        if (!exists) return@transaction null

        Orders.update({ Orders.id eq id }) {
            status?.let { v -> it[Orders.status] = v }
            paymentStatus?.let { v -> it[Orders.paymentStatus] = v }
            deliveryAddress?.let { v -> it[Orders.deliveryAddress] = v }
            notes?.let { v -> it[Orders.notes] = v }
            it[updatedAt] = LocalDateTime.now()
        }
        findById(id)
    }

    fun delete(id: Int): Boolean = transaction {
        Orders.deleteWhere { Orders.id eq id } > 0
    }

    private fun toDTO(row: ResultRow) = OrderDTO(
        id = row[Orders.id],
        userId = row[Orders.userId],
        restaurantId = row[Orders.restaurantId],
        status = row[Orders.status],
        totalAmount = row[Orders.totalAmount],
        deliveryAddress = row[Orders.deliveryAddress],
        paymentMethod = row[Orders.paymentMethod],
        paymentStatus = row[Orders.paymentStatus],
        notes = row[Orders.notes],
        createdAt = row[Orders.createdAt]
    )
}
