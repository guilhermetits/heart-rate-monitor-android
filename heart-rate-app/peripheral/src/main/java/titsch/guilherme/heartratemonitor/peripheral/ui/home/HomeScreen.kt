package titsch.guilherme.heartratemonitor.peripheral.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.koin.androidx.compose.koinViewModel
import titsch.guilherme.heartratemonitor.core.model.Requirement
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme
import titsch.guilherme.heartratemonitor.core.ui.ConnectionRequirementsActions
import titsch.guilherme.heartratemonitor.peripheral.R

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = koinViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val homeState by viewModel.homeState.collectAsState()

    HomeScreen(
        homeState = homeState,
        onConnectClick = { viewModel.allowConnections() },
        onDisconnectClick = { viewModel.denyConnections() },
        onAction = { viewModel.refreshRequirements() },
    )
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshRequirements()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun HomeScreen(
    homeState: HomeState,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = stringResource(id = R.string.home_screen_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier.fillMaxWidth()
        )
        ConnectionRequirementsActions(
            missingRequirements = homeState.missingRequirements,
            onBluetoothAction = onAction,
            onLocationAction = onAction,
            onPermissionAction = onAction
        )
        Column(
            verticalArrangement = Arrangement.SpaceAround,

            ) {
            ConnectedDevicesRow(
                homeState.connectedDevicesCount,
                Modifier.padding(DefaultPadding)
            )
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            AllowConnectionsRow(homeState.allowConnectionsEnabled, onConnectClick)
            DenyConnectionsRow(homeState.denyConnectionsEnabled, onDisconnectClick)
        }
    }
}

@Composable
fun AllowConnectionsRow(
    allowConnectionsEnabled: Boolean,
    onAllowConnectionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Button(
            onClick = onAllowConnectionsClick,
            enabled = allowConnectionsEnabled,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.allow_new_connections))
        }
    }
}

@Composable
fun DenyConnectionsRow(
    denyConnectionsEnabled: Boolean,
    onDenyConnectionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Button(
            onClick = onDenyConnectionsClick,
            enabled = denyConnectionsEnabled,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.deny_new_connections))
        }
    }
}

@Composable
fun ConnectedDevicesRow(connectedDevices: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Text(text = stringResource(id = R.string.connected_devices))
        Spacer(Modifier.width(12.dp))
        Text(connectedDevices.toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun HomePreview() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(initialHomeState, {}, {}, {}, Modifier.padding(innerPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun HomePreviewLight() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(initialHomeState, {}, {}, {}, Modifier.padding(innerPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun HomePreviewConnectedWithPermissions() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(
                homeState = HomeState(
                    allRequiredPermissionsGranted = true,
                    connectedDevicesCount = 2,
                    allowConnectionsEnabled = false,
                    denyConnectionsEnabled = true,
                    missingRequirements = listOf()
                ),
                onAction = {},
                onConnectClick = {},
                onDisconnectClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun HomePreviewDisconnectedWithPermissions() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(
                homeState = HomeState(
                    allRequiredPermissionsGranted = true,
                    connectedDevicesCount = 0,
                    allowConnectionsEnabled = true,
                    denyConnectionsEnabled = false,
                    missingRequirements = listOf(
                        Requirement.BLUETOOTH,
                        Requirement.BLUETOOTH_PERMISSION,
                        Requirement.LOCATION
                    )
                ),
                onAction = {},
                onConnectClick = {},
                onDisconnectClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

private val DefaultPadding = 12.dp