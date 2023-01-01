package titsch.guilherme.heartratemonitor.central.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme

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