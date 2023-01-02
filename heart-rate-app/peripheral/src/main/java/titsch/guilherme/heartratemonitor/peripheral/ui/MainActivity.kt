package titsch.guilherme.heartratemonitor.peripheral.ui

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
import org.koin.android.ext.android.inject
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme
import titsch.guilherme.heartratemonitor.peripheral.usecases.AllowConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.DenyConnectionsUseCase

class MainActivity : ComponentActivity() {
    private val allowConnectionsUseCase by inject<AllowConnectionsUseCase>()
    private val denyConnectionsUseCase by inject<DenyConnectionsUseCase>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeartRateMonitorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Peripheral")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        allowConnectionsUseCase()
    }

    override fun onStop() {
        super.onStop()
        denyConnectionsUseCase()
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "I'm the $name App!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HeartRateMonitorTheme {
        Greeting("Peripheral")
    }
}
