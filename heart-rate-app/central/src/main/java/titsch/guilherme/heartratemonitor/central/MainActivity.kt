package titsch.guilherme.heartratemonitor.central

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
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme

class MainActivity : ComponentActivity() {
    private val centralManager: CentralManager by inject()
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
        lifecycleScope.launch {
            centralManager.start()
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            centralManager.stop()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "I'm the $name App")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HeartRateMonitorTheme {
        Greeting("Central")
    }
}