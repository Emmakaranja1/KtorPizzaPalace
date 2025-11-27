package com.emmascode.config

import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.emmascode.models.*

object DatabaseFactory {

    // Explicitly point to your .env file directory
    private val env = dotenv {
        directory = "/home/emma/KtorPizzaPalace/backend"
        ignoreIfMissing = false
    }


    fun init() {
        val url = env["DB_URL"] ?: error("DB_URL not found in .env")
        val user = env["DB_USER"] ?: error("DB_USER not found in .env")
        val password = env["DB_PASSWORD"] ?: error("DB_PASSWORD not found in .env")
        val driver = env["DB_DRIVER"] ?: "org.postgresql.Driver"

        // Connect to PostgreSQL
        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        // Create tables
        transaction {
            SchemaUtils.create(
                Users,
                Pizzas,
                Restaurants,
                RestaurantPizzas,
                Orders,
                OrderItems
            )
        }

        println("✅ All tables created successfully!")
    }
}
