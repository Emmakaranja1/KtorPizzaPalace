package com.emmascode.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

object HttpClientManager {
    private const val CONFIG_FILE = ".pizza-cli-config"

    // Public properties
    var baseUrl = "http://localhost:8080"
    var authToken: String? = null

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.NONE // Change to LogLevel.ALL for debugging
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 30000
        }

        defaultRequest {
            url(baseUrl)
        }
    }

    init {
        loadConfig()
    }

    fun setToken(token: String) {
        authToken = token
        saveConfig()
    }

    fun clearToken() {
        authToken = null
        saveConfig()
    }

    fun isAuthenticated(): Boolean = authToken != null

    private fun getConfigFile(): File {
        val homeDir = System.getProperty("user.home")
        return File(homeDir, CONFIG_FILE)
    }

    private fun saveConfig() {
        val config = buildString {
            appendLine("BASE_URL=$baseUrl")
            authToken?.let { appendLine("AUTH_TOKEN=$it") }
        }
        getConfigFile().writeText(config)
    }

    private fun loadConfig() {
        val configFile = getConfigFile()
        if (configFile.exists()) {
            configFile.readLines().forEach { line ->
                when {
                    line.startsWith("BASE_URL=") -> baseUrl = line.substringAfter("=")
                    line.startsWith("AUTH_TOKEN=") -> authToken = line.substringAfter("=")
                }
            }
        }
    }

    // HTTP Methods
    suspend inline fun <reified T> get(path: String, requireAuth: Boolean = false): Result<ApiResponse<T>> {
        return try {
            val response: HttpResponse = client.get(path) {
                if (requireAuth && authToken != null) {
                    header("Authorization", "Bearer $authToken")
                }
            }
            if (response.status.isSuccess()) {
                Result.success(response.body<ApiResponse<T>>())
            } else {
                Result.failure(Exception("HTTP ${response.status.value}: ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend inline fun <reified T, reified R> post(path: String, body: T, requireAuth: Boolean = false): Result<ApiResponse<R>> {
        return try {
            val response: HttpResponse = client.post(path) {
                contentType(ContentType.Application.Json)
                setBody(body)
                if (requireAuth && authToken != null) {
                    header("Authorization", "Bearer $authToken")
                }
            }
            if (response.status.isSuccess()) {
                Result.success(response.body<ApiResponse<R>>())
            } else {
                Result.failure(Exception("HTTP ${response.status.value}: ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend inline fun <reified T, reified R> put(path: String, body: T, requireAuth: Boolean = true): Result<ApiResponse<R>> {
        return try {
            val response: HttpResponse = client.put(path) {
                contentType(ContentType.Application.Json)
                setBody(body)
                if (requireAuth && authToken != null) {
                    header("Authorization", "Bearer $authToken")
                }
            }
            if (response.status.isSuccess()) {
                Result.success(response.body<ApiResponse<R>>())
            } else {
                Result.failure(Exception("HTTP ${response.status.value}: ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(path: String, requireAuth: Boolean = true): Result<ApiResponse<Boolean>> {
        return try {
            val response: HttpResponse = client.delete(path) {
                if (requireAuth && authToken != null) {
                    header("Authorization", "Bearer $authToken")
                }
            }
            if (response.status.isSuccess()) {
                Result.success(response.body<ApiResponse<Boolean>>())
            } else {
                Result.failure(Exception("HTTP ${response.status.value}: ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}




