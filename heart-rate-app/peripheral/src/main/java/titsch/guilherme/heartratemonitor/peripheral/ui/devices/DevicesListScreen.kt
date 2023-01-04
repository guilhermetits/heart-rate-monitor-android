package titsch.guilherme.heartratemonitor.peripheral.ui.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import titsch.guilherme.heartratemonitor.core.model.ConnectedDevice
import titsch.guilherme.heartratemonitor.peripheral.R

@Composable
fun DevicesListRoute(
    viewModel: DevicesListViewModel = koinViewModel()
) {
    val list by viewModel.connectedDevicesListStateFlow.collectAsState()
    val lazyListState = rememberLazyListState()

    DevicesListScreen(itemsList = list, lazyListState = lazyListState)
}

@Composable
fun DevicesListScreen(
    itemsList: List<ConnectedDevice>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.devices_screen_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        LazyColumn(
            state = lazyListState
        ) {
            items(items = itemsList, key = { it.address }) {
                Card(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    ConnectedDevice(connectedDevice = it)
                }
            }
        }
    }
}

@Composable
fun ConnectedDevice(
    connectedDevice: ConnectedDevice,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(24.dp)
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                connectedDevice.name ?: stringResource(id = R.string.name_not_found),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp)
            )
            Text(
                connectedDevice.alias ?: stringResource(id = R.string.alias_not_found),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp)
            )
            Text(
                connectedDevice.address,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
            )
        }
    }
}