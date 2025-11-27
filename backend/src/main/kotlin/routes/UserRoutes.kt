package com.emmascode.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import com.emmascode.services.UserService
import com.emmascode.dto.*
import com.emmascode.utils.ResponseHelper

fun Route.userRoutes() {
    val service = UserService()

    route("/users") {
        get {
            val users = service.getAllUsers()
            call.respond(ResponseHelper.success(users))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val user = service.getUserById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("User not found"))

            call.respond(ResponseHelper.success(user))
        }

        post {
            val dto = call.receive<CreateUserDTO>()
            val user = service.createUser(dto)
            call.respond(HttpStatusCode.Created, ResponseHelper.success(user, "User created successfully"))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val dto = call.receive<UpdateUserDTO>()
            val user = service.updateUser(id, dto)
                ?: return@put call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("User not found"))

            call.respond(ResponseHelper.success(user, "User updated successfully"))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseHelper.error<String>("Invalid ID"))

            val deleted = service.deleteUser(id)
            if (deleted) {
                call.respond(ResponseHelper.success(true, "User deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, ResponseHelper.error<String>("User not found"))
            }
        }
    }

    post("/auth/register") {
        val dto = call.receive<CreateUserDTO>()
        val user = service.createUser(dto)
        call.respond(HttpStatusCode.Created, ResponseHelper.success(user, "User registered successfully"))
    }

    post("/auth/login") {
        val dto = call.receive<LoginDTO>()
        val response = service.login(dto)
            ?: return@post call.respond(HttpStatusCode.Unauthorized, ResponseHelper.error<String>("Invalid credentials"))

        call.respond(ResponseHelper.success(response, "Login successful"))
    }
}