package com.nammahasiru.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nammahasiru.app.data.auth.AuthRepository
import com.nammahasiru.app.data.auth.AuthResult
import com.nammahasiru.app.data.auth.SessionManager
import com.nammahasiru.app.data.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class AuthUiState(
    val isLoading: Boolean       = false,
    val errorMessage: String?    = null,
    val isSuccess: Boolean       = false
)

// ── Validation helpers ────────────────────────────────────────────────────────

object PasswordStrength {
    enum class Level { EMPTY, WEAK, FAIR, STRONG, VERY_STRONG }

    data class Result(
        val level: Level,
        val progress: Float,          // 0f–1f for progress bar
        val label: String,
        val hasUppercase: Boolean,
        val hasLowercase: Boolean,
        val hasDigit: Boolean,
        val hasSpecial: Boolean,
        val hasMinLength: Boolean
    )

    fun evaluate(password: String): Result {
        if (password.isEmpty()) return Result(Level.EMPTY, 0f, "", false, false, false, false, false)

        val hasUpper   = password.any { it.isUpperCase() }
        val hasLower   = password.any { it.isLowerCase() }
        val hasDigit   = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val hasLen     = password.length >= 8

        val score = listOf(hasUpper, hasLower, hasDigit, hasSpecial, hasLen).count { it }

        val (level, progress, label) = when (score) {
            0, 1 -> Triple(Level.WEAK,       0.20f, "Weak")
            2    -> Triple(Level.FAIR,       0.45f, "Fair")
            3    -> Triple(Level.STRONG,     0.70f, "Strong")
            4    -> Triple(Level.STRONG,     0.85f, "Strong")
            else -> Triple(Level.VERY_STRONG, 1.00f, "Very Strong")
        }
        return Result(level, progress, label, hasUpper, hasLower, hasDigit, hasSpecial, hasLen)
    }
}

fun isValidEmail(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

fun isStrongPassword(password: String): Boolean {
    val r = PasswordStrength.evaluate(password)
    return r.hasUppercase && r.hasLowercase && r.hasDigit && r.hasSpecial && r.hasMinLength
}

// ── ViewModel ────────────────────────────────────────────────────────────────

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val db      = AppDatabase.getDatabase(application)
    private val repo    = AuthRepository(db.userDao())
    val session         = SessionManager(application)

    private val _loginState    = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(AuthUiState())
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _loginState.value = AuthUiState(isLoading = true)

            // Basic client-side validation
            if (email.isBlank() || password.isBlank()) {
                _loginState.value = AuthUiState(errorMessage = "Please fill in all fields.")
                return@launch
            }
            if (!isValidEmail(email)) {
                _loginState.value = AuthUiState(errorMessage = "Please enter a valid email address.")
                return@launch
            }

            when (val result = repo.login(email, password)) {
                is AuthResult.Success -> {
                    session.saveSession(
                        userId    = result.user.id,
                        email     = result.user.email,
                        firstName = result.user.firstName,
                        lastName  = result.user.lastName,
                        rememberMe = rememberMe
                    )
                    _loginState.value = AuthUiState(isSuccess = true)
                }
                is AuthResult.Error -> {
                    _loginState.value = AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _registerState.value = AuthUiState(isLoading = true)

            // Validation
            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                _registerState.value = AuthUiState(errorMessage = "Please fill in all fields.")
                return@launch
            }
            if (!isValidEmail(email)) {
                _registerState.value = AuthUiState(errorMessage = "Please enter a valid email address.")
                return@launch
            }
            if (!isStrongPassword(password)) {
                _registerState.value = AuthUiState(
                    errorMessage = "Password must be ≥ 8 chars and contain uppercase, lowercase, number and special character."
                )
                return@launch
            }
            if (password != confirmPassword) {
                _registerState.value = AuthUiState(errorMessage = "Passwords do not match.")
                return@launch
            }

            when (val result = repo.register(firstName, lastName, email, password)) {
                is AuthResult.Success -> {
                    session.saveSession(
                        userId    = result.user.id,
                        email     = result.user.email,
                        firstName = result.user.firstName,
                        lastName  = result.user.lastName,
                        rememberMe = true
                    )
                    _registerState.value = AuthUiState(isSuccess = true)
                }
                is AuthResult.Error -> {
                    _registerState.value = AuthUiState(errorMessage = result.message)
                }
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    fun logout() {
        session.clearSession()
        _loginState.value    = AuthUiState()
        _registerState.value = AuthUiState()
    }

    // ── Reset error state ─────────────────────────────────────────────────────

    fun clearLoginError()    { _loginState.value    = _loginState.value.copy(errorMessage = null) }
    fun clearRegisterError() { _registerState.value = _registerState.value.copy(errorMessage = null) }
}
