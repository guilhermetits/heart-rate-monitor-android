package titsch.guilherme.heartratemonitor.peripheral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import titsch.guilherme.heartratemonitor.bluetooth.PeripheralManager
import titsch.guilherme.heartratemonitor.peripheral.ui.theme.heartRateMonitorTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private var peripheralManager: PeripheralManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            heartRateMonitorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    greeting("Peripheral")
                }
            }
        }
        peripheralManager = PeripheralManager(this)
    }

    override fun onStart() {
        super.onStart()
        peripheralManager?.start()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            while (true) {
                delay(timeMillis = 10000)
                peripheralManager?.emitHeartRate(Random.nextInt(from = 40, until = 190))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        peripheralManager?.stop()
    }
}

@Composable
fun greeting(name: String) {
    Text(text = "I'm the $name App!")
}

@Preview(showBackground = true)
@Composable
fun defaultPreview() {
    heartRateMonitorTheme {
        greeting("Peripheral")
    }
}
