package com.nammahasiru.app.data.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user session using encrypted SharedPreferences.
 * Persists login state across app restarts.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME        = "namma_hasiru_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID      = "user_id"
        private const val KEY_USER_EMAIL   = "user_email"
        private const val KEY_USER_FNAME   = "user_first_name"
        private const val KEY_USER_LNAME   = "user_last_name"
        private const val KEY_REMEMBER_ME  = "remember_me"
    }

    /** Saves a logged-in session. */
    fun saveSession(
        userId: Int,
        email: String,
        firstName: String,
        lastName: String,
        rememberMe: Boolean = true
    ) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_FNAME, firstName)
            putString(KEY_USER_LNAME, lastName)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            apply()
        }
    }

    /** Clears session on logout. */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int      = prefs.getInt(KEY_USER_ID, -1)
    fun getEmail(): String    = prefs.getString(KEY_USER_EMAIL, "") ?: ""
    fun getFirstName(): String = prefs.getString(KEY_USER_FNAME, "") ?: ""
    fun getLastName(): String  = prefs.getString(KEY_USER_LNAME, "") ?: ""
    fun getFullName(): String  = "${getFirstName()} ${getLastName()}".trim()
    fun isRememberMe(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)
}
