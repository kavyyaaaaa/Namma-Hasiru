package com.nammahasiru.app.data.auth

import com.nammahasiru.app.data.database.UserDao
import com.nammahasiru.app.data.database.UserEntity
import java.security.MessageDigest

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String)    : AuthResult()
}

class AuthRepository(private val userDao: UserDao) {

    // ── Password hashing ─────────────────────────────────────────────────────

    private fun hashPassword(password: String): String {
        val digest  = MessageDigest.getInstance("SHA-256")
        val bytes   = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // ── Registration ─────────────────────────────────────────────────────────

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): AuthResult {
        val trimmedEmail = email.trim().lowercase()

        // Check duplicate
        if (userDao.countByEmail(trimmedEmail) > 0) {
            return AuthResult.Error("An account with this email already exists.")
        }

        val entity = UserEntity(
            firstName    = firstName.trim(),
            lastName     = lastName.trim(),
            email        = trimmedEmail,
            passwordHash = hashPassword(password)
        )

        return try {
            val id = userDao.insertUser(entity)
            val inserted = userDao.getUserById(id.toInt())!!
            AuthResult.Success(inserted)
        } catch (e: Exception) {
            AuthResult.Error("Registration failed. Please try again.")
        }
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return AuthResult.Error("No account found with this email.")

        return if (user.passwordHash == hashPassword(password)) {
            AuthResult.Success(user)
        } else {
            AuthResult.Error("Incorrect password. Please try again.")
        }
    }

    // ── Email check ───────────────────────────────────────────────────────────

    suspend fun emailExists(email: String): Boolean =
        userDao.countByEmail(email.trim().lowercase()) > 0
}
