package titsch.guilherme.heartratemonitor.central.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import titsch.guilherme.heartratemonitor.central.ui.home.Greeting
import titsch.guilherme.heartratemonitor.central.usecases.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.DisconnectDeviceUseCase
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme

class MainActivity : ComponentActivity() {
    private val connectDeviceUseCase by inject<ConnectDeviceUseCase>()
    private val disconnectDeviceUseCase by inject<DisconnectDeviceUseCase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeartRateMonitorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Central")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { connectDeviceUseCase() }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch { disconnectDeviceUseCase() }
    }
}

