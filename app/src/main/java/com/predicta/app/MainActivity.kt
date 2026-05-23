package com.predicta.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.settings.AppSettingsRepository
import com.predicta.app.ui.PredictaScaffold
import com.predicta.app.ui.theme.PredictaTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: AppSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsRepository.settings.collectAsStateWithLifecycle()

            PredictaTheme(themeMode = settings.themeMode) {
                PredictaScaffold()
            }
        }
    }
}
