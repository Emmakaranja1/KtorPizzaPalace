package com.emmascode.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object OrderItems : Table("order_items") {
    val id = integer("id").autoIncrement()
    val orderId = integer("order_id").references(Orders.id)
    val pizzaId = integer("pizza_id").references(Pizzas.id)
    val quantity = integer("quantity").default(1)
    val pricePerItem = decimal("price_per_item", 10, 2)
    val subtotal = decimal("subtotal", 10, 2)
    val specialInstructions = text("special_instructions").nullable()
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}