package titsch.guilherme.heartratemonitor.central.ui.heartratelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import titsch.guilherme.heartratemonitor.central.R
import titsch.guilherme.heartratemonitor.central.ui.component.HeartRateMeasurement
import titsch.guilherme.heartratemonitor.central.ui.component.HeartRateMeasurementUIModel

@Composable
fun HeartRateListRoute(
    viewModel: HeartRateListViewModel = koinViewModel()
) {
    val list by viewModel.heartRateListFlow.collectAsState()
    val lazyListState = rememberLazyListState()

    HeartRateListScreen(itemsList = list, lazyListState = lazyListState)
}

@Composable
fun HeartRateListScreen(
    itemsList: List<HeartRateMeasurementUIModel>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.heart_rate_screen_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        LazyColumn(
            state = lazyListState
        ) {
            items(items = itemsList, key = { it.id }) {
                Card(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    HeartRateMeasurement(heartRateMeasurement = it)
                }
            }
        }
    }
}
