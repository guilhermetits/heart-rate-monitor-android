package titsch.guilherme.heartratemonitor.central.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import titsch.guilherme.heartratemonitor.central.usecases.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.DisconnectDeviceUseCase

class MainActivity : ComponentActivity() {
    private val connectDeviceUseCase by inject<ConnectDeviceUseCase>()
    private val disconnectDeviceUseCase by inject<DisconnectDeviceUseCase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CentralApp()
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

