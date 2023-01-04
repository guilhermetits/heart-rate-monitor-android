package titsch.guilherme.heartratemonitor.central.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import titsch.guilherme.heartratemonitor.central.R
import titsch.guilherme.heartratemonitor.central.ui.component.HeartRateMeasurement
import titsch.guilherme.heartratemonitor.central.ui.component.HeartRateMeasurementUIModel
import titsch.guilherme.heartratemonitor.core.model.ConnectionState
import titsch.guilherme.heartratemonitor.core.model.Requirement
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme
import titsch.guilherme.heartratemonitor.core.ui.ConnectionRequirementsActions

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = koinViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val homeState by viewModel.homeState.collectAsState()

    HomeScreen(
        homeState = homeState,
        onConnectClick = { viewModel.connect() },
        onDisconnectClick = { viewModel.disconnect() },
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
            .verticalScroll(rememberScrollState())
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
            ConnectionStateRow(
                homeState.connectionState,
                Modifier.padding(DefaultPadding)
            )
            homeState.heartRateMeasurement?.let {
                HeartRateMeasurement(heartRateMeasurement = it)
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            ConnectRow(homeState.connectEnabled, onConnectClick)
            DisconnectRow(homeState.disconnectEnabled, onDisconnectClick)
        }
    }
}

@Composable
fun ConnectRow(connectEnabled: Boolean, onConnectClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Button(
            onClick = onConnectClick,
            enabled = connectEnabled,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.connect))
        }
    }
}

@Composable
fun DisconnectRow(
    disconnectEnabled: Boolean, onDisconnectClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Button(
            onClick = onDisconnectClick,
            enabled = disconnectEnabled,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.disconnect))
        }
    }
}

@Composable
fun ConnectionStateRow(connectionState: ConnectionState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Text(connectionState.toString(), style = MaterialTheme.typography.bodyLarge)
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
                    connectionState = ConnectionState.CONNECTED,
                    connectEnabled = false,
                    disconnectEnabled = true,
                    missingRequirements = listOf(),
                    heartRateMeasurement = HeartRateMeasurementUIModel(
                        0,
                        "110 bpm",
                        "2023-01-04 11:40"
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun HomePreviewDisconnectedWithPermissions() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(
                homeState = HomeState(
                    allRequiredPermissionsGranted = true,
                    connectionState = ConnectionState.DISCONNECTED,
                    connectEnabled = true,
                    disconnectEnabled = false,
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