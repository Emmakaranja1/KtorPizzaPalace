package com.emmascode.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.emmascode.services.RestaurantService
import com.emmascode.dto.*
import com.emmascode.utils.ResponseHelper

fun Route.restaurantRoutes() {
    val service = RestaurantService()

    route("/restaurants") {
        get {
            val active = call.request.queryParameters["active"]?.toBoolean()
            val ownerId = call.request.queryParameters["ownerId"]?.toIntOrNull()

            val restaurants = when {
                active == true -> service.getActiveRestaurants()
                ownerId != null -> service.getRestaurantsByOwnerId(ownerId)
                else -> service.getAllRestaurants()
            }
            call.respond(ResponseHelper.success(restaurants))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val restaurant = service.getRestaurantById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Restaurant not found"))

            call.respond(ResponseHelper.success(restaurant))
        }

        post {
            val dto = call.receive<CreateRestaurantDTO>()
            val restaurant = service.createRestaurant(dto)
            call.respond(HttpStatusCode.Created, ResponseHelper.success(restaurant, "Restaurant created successfully"))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val dto = call.receive<UpdateRestaurantDTO>()
            val restaurant = service.updateRestaurant(id, dto)
                ?: return@put call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Restaurant not found"))

            call.respond(ResponseHelper.success(restaurant, "Restaurant updated successfully"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val deleted = service.deleteRestaurant(id)
            if (deleted) {
                call.respond(ResponseHelper.success(true, "Restaurant deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Restaurant not found"))
            }
        }
    }
}

