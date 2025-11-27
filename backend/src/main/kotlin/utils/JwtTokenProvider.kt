package com.emmascode.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.cdimascio.dotenv.dotenv
import java.util.*

object JwtTokenProvider {
    private val env = dotenv()
    private val secret = env["JWT_SECRET"] ?: "your-secret-key-change-in-production"
    private val issuer = env["JWT_ISSUER"] ?: "pizza-ordering-api"
    private val validityInMs = env["JWT_VALIDITY_MS"]?.toLongOrNull() ?: 86400000L // 24 hours default

    // Expose the algorithm for Ktor JWT
    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    /**
     * Generate a JWT token for a user
     */
    fun generateToken(userId: Int, email: String, role: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .withIssuedAt(Date())
            .sign(algorithm)
    }

    /**
     * Verify and decode a JWT token
     */
    fun verifyToken(token: String): DecodedToken? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build()

            val decodedJWT = verifier.verify(token)
            DecodedToken(
                userId = decodedJWT.subject.toInt(),
                email = decodedJWT.getClaim("email").asString(),
                role = decodedJWT.getClaim("role").asString()
            )
        } catch (e: JWTVerificationException) {
            null
        }
    }

    data class DecodedToken(
        val userId: Int,
        val email: String,
        val role: String
    )
}
