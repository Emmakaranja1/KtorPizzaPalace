package com.emmascode.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.emmascode.services.OrderItemService
import com.emmascode.dto.*
import com.emmascode.utils.ResponseHelper

fun Route.orderItemRoutes() {
    val service = OrderItemService()

    route("/order-items") {
        get {
            val orderId = call.request.queryParameters["orderId"]?.toIntOrNull()

            val items = if (orderId != null) {
                service.getOrderItemsByOrderId(orderId)
            } else {
                service.getAllOrderItems()
            }
            call.respond(ResponseHelper.success(items))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val item = service.getOrderItemById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Order item not found"))

            call.respond(ResponseHelper.success(item))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val dto = call.receive<UpdateOrderItemDTO>()
            val item = service.updateOrderItem(id, dto)
                ?: return@put call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Order item not found"))

            call.respond(ResponseHelper.success(item, "Order item updated successfully"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val deleted = service.deleteOrderItem(id)
            if (deleted) {
                call.respond(ResponseHelper.success(true, "Order item deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("Order item not found"))
            }
        }
    }
}