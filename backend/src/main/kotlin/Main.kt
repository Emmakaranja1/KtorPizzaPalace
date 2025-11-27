package com.emmascode

import com.emmascode.config.DatabaseFactory
import com.emmascode.routes.orderItemRoutes
import com.emmascode.routes.orderRoutes
import com.emmascode.routes.pizzaRoutes
import com.emmascode.routes.restaurantPizzaRoutes
import com.emmascode.routes.restaurantRoutes
import com.emmascode.routes.userRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main() {
    // Initialize database and create tables
    DatabaseFactory.init()

    // Start Ktor Server
    embeddedServer(Netty, host = "0.0.0.0", port = 8080) {
        install(ContentNegotiation) { json() }

        routing {
            userRoutes()
            pizzaRoutes()
            orderRoutes()
            restaurantRoutes()
            restaurantPizzaRoutes()
            orderItemRoutes()
            get("/") {
                call.respond(
                    mapOf(
                        "status" to "Backend running 🎉",
                        "db" to "Connected successfully"
                    )
                )
            }

            // You can add your other routes here, e.g.:
            // userRoutes()
            // pizzaRoutes()
            // restaurantRoutes()
        }
    }.start(wait = true)
}

