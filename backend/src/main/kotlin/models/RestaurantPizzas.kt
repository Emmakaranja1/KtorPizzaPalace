package com.emmascode.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object RestaurantPizzas : Table("restaurant_pizzas") {
    val id = integer("id").autoIncrement()
    val restaurantId = integer("restaurant_id").references(Restaurants.id)
    val pizzaId = integer("pizza_id").references(Pizzas.id)
    val price = decimal("price", 10, 2)
    val isAvailable = bool("is_available").default(true)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(restaurantId, pizzaId)
    }
}