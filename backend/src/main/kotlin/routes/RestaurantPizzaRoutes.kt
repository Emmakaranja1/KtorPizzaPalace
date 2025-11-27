package com.emmascode.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.emmascode.services.RestaurantPizzaService
import com.emmascode.dto.*
import com.emmascode.utils.ResponseHelper

fun Route.restaurantPizzaRoutes() {
    val service = RestaurantPizzaService()

    route("/restaurant-pizzas") {
        get {
            val restaurantId = call.request.queryParameters["restaurantId"]?.toIntOrNull()
            val pizzaId = call.request.queryParameters["pizzaId"]?.toIntOrNull()

            val items = when {
                restaurantId != null -> service.getRestaurantPizzasByRestaurantId(restaurantId)
                pizzaId != null -> service.getRestaurantPizzasByPizzaId(pizzaId)
                else -> service.getAllRestaurantPizzas()
            }
            call.respond(ResponseHelper.success(items))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val item = service.getRestaurantPizzaById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Restaurant pizza not found"))

            call.respond(ResponseHelper.success(item))
        }

        post {
            val dto = call.receive<CreateRestaurantPizzaDTO>()
            val item = service.createRestaurantPizza(dto)
            call.respond(HttpStatusCode.Created, ResponseHelper.success(item, "Restaurant pizza created successfully"))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val dto = call.receive<UpdateRestaurantPizzaDTO>()
            val item = service.updateRestaurantPizza(id, dto)
                ?: return@put call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Restaurant pizza not found"))

            call.respond(ResponseHelper.success(item, "Restaurant pizza updated successfully"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val deleted = service.deleteRestaurantPizza(id)
            if (deleted) {
                call.respond(ResponseHelper.success(true, "Restaurant pizza deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Restaurant pizza not found"))
            }
        }
    }
}