package com.emmascode.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.emmascode.services.OrderService
import com.emmascode.dto.*
import com.emmascode.utils.ResponseHelper

fun Route.orderRoutes() {
    val service = OrderService()

    route("/orders") {

        // ------------------------------------------------------------
        // GET /orders?userId=&restaurantId=&status=
        // ------------------------------------------------------------
        get {
            try {
                val userId = call.request.queryParameters["userId"]?.toIntOrNull()
                val restaurantId = call.request.queryParameters["restaurantId"]?.toIntOrNull()
                val status = call.request.queryParameters["status"]

                val orders = when {
                    userId != null -> service.getOrdersByUserId(userId)
                    restaurantId != null -> service.getOrdersByRestaurantId(restaurantId)
                    status != null -> service.getOrdersByStatus(status)
                    else -> service.getAllOrders()
                }

                call.respond(ResponseHelper.success(orders))

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ResponseHelper.error<String>("Failed to fetch orders: ${e.message}")
                )
            }
        }

        // ------------------------------------------------------------
        // GET /orders/{id}
        // ------------------------------------------------------------
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseHelper.error<String>("Invalid order ID")
                )

            val order = service.getOrderById(id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ResponseHelper.error<String>("Order not found")
                )

            call.respond(ResponseHelper.success(order))
        }

        // ------------------------------------------------------------
        // POST /orders
        // Create a new order
        // ------------------------------------------------------------
        post {
            try {
                val dto = call.receive<CreateOrderDTO>()

                // Validate input before calling service
                if (dto.items.isEmpty()) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ResponseHelper.error<String>("Order must contain at least one item")
                    )
                }

                if (dto.items.any { it.quantity <= 0 }) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ResponseHelper.error<String>("Item quantity must be at least 1")
                    )
                }

                val order = service.createOrder(dto)

                call.respond(
                    HttpStatusCode.Created,
                    ResponseHelper.success(order, "Order created successfully")
                )

            } catch (e: IllegalArgumentException) {
                // From service (e.g., pizza not found, restaurant mismatch)
                call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseHelper.error<String>(e.message ?: "Invalid order data")
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ResponseHelper.error<String>("Failed to create order: ${e.message}")
                )
            }
        }

        // ------------------------------------------------------------
        // PUT /orders/{id}
        // Update order status, payment status, delivery address, notes
        // ------------------------------------------------------------
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseHelper.error<String>("Invalid order ID")
                )

            try {
                val dto = call.receive<UpdateOrderDTO>()
                val order = service.updateOrder(id, dto)
                    ?: return@put call.respond(
                        HttpStatusCode.NotFound,
                        ResponseHelper.error<String>("Order not found")
                    )

                call.respond(
                    ResponseHelper.success(order, "Order updated successfully")
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ResponseHelper.error<String>("Failed to update order: ${e.message}")
                )
            }
        }

        // ------------------------------------------------------------
        // DELETE /orders/{id}
        // ------------------------------------------------------------
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ResponseHelper.error<String>("Invalid order ID")
                )

            try {
                val deleted = service.deleteOrder(id)

                if (!deleted) {
                    return@delete call.respond(
                        HttpStatusCode.NotFound,
                        ResponseHelper.error<String>("Order not found")
                    )
                }

                call.respond(
                    ResponseHelper.success(true, "Order deleted successfully")
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ResponseHelper.error<String>("Failed to delete order: ${e.message}")
                )
            }
        }
    }
}
