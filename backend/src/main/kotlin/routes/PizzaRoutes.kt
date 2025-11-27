package com.emmascode.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.emmascode.services.PizzaService
import com.emmascode.dto.*
import com.emmascode.utils.ResponseHelper

fun Route.pizzaRoutes() {
    val service = PizzaService()

    route("/pizzas") {
        get {
            val category = call.request.queryParameters["category"]
            val pizzas = if (category != null) {
                service.getPizzasByCategory(category)
            } else {
                service.getAllPizzas()
            }
            call.respond(ResponseHelper.success(pizzas))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val pizza = service.getPizzaById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Pizza not found"))

            call.respond(ResponseHelper.success(pizza))
        }

        post {
            val dto = call.receive<CreatePizzaDTO>()
            val pizza = service.createPizza(dto)
            call.respond(HttpStatusCode.Created, ResponseHelper.success(pizza, "Pizza created successfully"))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val dto = call.receive<UpdatePizzaDTO>()
            val pizza = service.updatePizza(id, dto)
                ?: return@put call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Pizza not found"))

            call.respond(ResponseHelper.success(pizza, "Pizza updated successfully"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val deleted = service.deletePizza(id)
            if (deleted) {
                call.respond(ResponseHelper.success(true, "Pizza deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Pizza not found"))
            }
        }
    }
}