package com.predicta.app.ui

import androidx.lifecycle.ViewModel
import com.predicta.app.core.network.NetworkMonitor
import com.predicta.app.feature_auth.data.session.UserSession
import com.predicta.app.feature_auth.data.session.UserSessionManager
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    val session: StateFlow<UserSession> = sessionManager.session

    fun retryNetwork() {
        networkMonitor.refresh()
    }
}
