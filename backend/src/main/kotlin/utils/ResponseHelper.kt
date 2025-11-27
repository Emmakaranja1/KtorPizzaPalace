package com.emmascode.utils

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

object ResponseHelper {
    fun <T> success(data: T, message: String? = null): ApiResponse<T> {
        return ApiResponse(
            success = true,
            message = message,
            data = data
        )
    }

    fun <T> error(message: String, error: String? = null): ApiResponse<T> {
        return ApiResponse(
            success = false,
            message = message,
            error = error
        )
    }

    fun <T> paginated(
        data: List<T>,
        page: Int,
        pageSize: Int,
        totalItems: Int
    ): PaginatedResponse<T> {
        val totalPages = (totalItems + pageSize - 1) / pageSize
        return PaginatedResponse(
            success = true,
            data = data,
            page = page,
            pageSize = pageSize,
            totalItems = totalItems,
            totalPages = totalPages
        )
    }
}