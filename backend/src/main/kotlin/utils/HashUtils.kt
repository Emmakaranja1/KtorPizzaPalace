package com.emmascode.utils

import org.mindrot.jbcrypt.BCrypt

object HashUtils {
    /**
     * Hash a plain text password using BCrypt
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    /**
     * Verify a plain text password against a hashed password
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
}