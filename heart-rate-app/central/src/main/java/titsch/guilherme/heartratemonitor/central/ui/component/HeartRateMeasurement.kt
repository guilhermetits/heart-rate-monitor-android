package titsch.guilherme.heartratemonitor.central.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeartRateMeasurement(
    heartRateMeasurement: HeartRateMeasurementUIModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(24.dp)
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                heartRateMeasurement.value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp)
            )
            Text(
                heartRateMeasurement.date,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}