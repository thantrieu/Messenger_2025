package pro.branium.messenger.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pro.branium.messenger.presentation.navigation.AppNavigation
import pro.branium.messenger.presentation.theme.Messenger2025Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Messenger2025Theme {
                AppNavigation()
            }
        }
    }
}
