package com.predicta.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.predicta.app.ui.PredictaScaffold
import com.predicta.app.ui.theme.PredictaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PredictaTheme {
                PredictaScaffold()
            }
        }
    }
}
