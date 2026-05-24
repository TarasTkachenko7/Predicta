package com.predicta.app.feature_auth.data.session

import android.content.Context
import com.predicta.app.feature_auth.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserSessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _session = MutableStateFlow(
        UserSession(
            isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false),
            userName = preferences.getString(KEY_USER_NAME, "").orEmpty(),
            email = preferences.getString(KEY_EMAIL, "").orEmpty(),
            role = preferences.getString(KEY_ROLE, "").orEmpty(),
        ),
    )
    val session: StateFlow<UserSession> = _session.asStateFlow()

    fun startSession(user: User) {
        preferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_ROLE, user.role)
            .apply()

        _session.update {
            it.copy(
                isLoggedIn = true,
                userName = user.name,
                email = user.email,
                role = user.role,
            )
        }
    }

    fun clearSession() {
        preferences.edit()
            .remove(KEY_IS_LOGGED_IN)
            .remove(KEY_USER_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_ROLE)
            .apply()

        _session.value = UserSession()
    }

    private companion object {
        const val PREFERENCES_NAME = "predicta_session"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_NAME = "user_name"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
    }
}

data class UserSession(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val email: String = "",
    val role: String = "",
)
