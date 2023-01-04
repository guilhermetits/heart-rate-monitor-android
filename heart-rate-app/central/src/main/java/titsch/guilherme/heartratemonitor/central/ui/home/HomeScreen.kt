package titsch.guilherme.heartratemonitor.central.ui.home

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothActivityContract
import titsch.guilherme.heartratemonitor.core.bluetooth.LocationActivityContract
import titsch.guilherme.heartratemonitor.core.model.ConnectionState
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = koinViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val homeState by viewModel.homeState.collectAsState()

    val launcherBluetooth = rememberLauncherForActivityResult(BluetoothActivityContract()) {
        viewModel.refreshRequirements()
    }
    val launcherLocation = rememberLauncherForActivityResult(LocationActivityContract()) {
        viewModel.refreshRequirements()
    }
    val launcherPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.refreshRequirements()
        }

    HomeScreen(
        homeState = homeState,
        onBluetoothClick = { launcherBluetooth.launch() },
        onLocationClick = { launcherLocation.launch() },
        onPermissionsClick = { launcherPermission.launch(permission) },
        onConnectClick = { viewModel.connect() },
        onDisconnectClick = { viewModel.disconnect() },
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
    onBluetoothClick: () -> Unit,
    onLocationClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.home_screen_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier.fillMaxWidth()
        )
        Spacer(Modifier.width(32.dp))
        PermissionsRow(
            missingRequirements = homeState.missingRequirements,
            onBluetoothClick = onBluetoothClick,
            onLocationClick = onLocationClick,
            onPermissionsClick = onPermissionsClick,
        )
        Spacer(Modifier.width(12.dp))
        ConnectRow(homeState.connectEnabled, onConnectClick)
        Spacer(Modifier.width(12.dp))
        DisconnectRow(homeState.disconnectEnabled, onDisconnectClick)
        Spacer(Modifier.width(12.dp))
        ConnectionStateRow(homeState.connectionState)
    }
}

@Composable
fun PermissionsRow(
    missingRequirements: List<Requirement>,
    onBluetoothClick: () -> Unit,
    onLocationClick: () -> Unit,
    onPermissionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            RequirementRequest(
                requirementText = stringResource(id = R.string.enable_bluetooth),
                isMissing = missingRequirements.contains(Requirement.BLUETOOTH),
                onRequirementClick = onBluetoothClick
            )
            RequirementRequest(
                requirementText = stringResource(id = R.string.enable_location),
                isMissing = missingRequirements.contains(Requirement.LOCATION),
                onRequirementClick = onLocationClick
            )
            RequirementRequest(
                requirementText = stringResource(id = R.string.grant_permission),
                isMissing = missingRequirements.contains(Requirement.BLUETOOTH_PERMISSION),
                onRequirementClick = onPermissionsClick
            )
        }
    }
}

@Composable
fun RequirementRequest(
    requirementText: String,
    isMissing: Boolean,
    onRequirementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (!isMissing) Icons.Default.Check to MaterialTheme.colorScheme.tertiary
    else Icons.Default.Warning to MaterialTheme.colorScheme.error
    Row(
        modifier = modifier.padding(DefaultPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon.first,
            contentDescription = "",
            tint = icon.second,
            modifier = Modifier.padding(
                DefaultPadding
            )
        )
        Button(
            onClick = onRequirementClick, enabled = isMissing, modifier = Modifier.height(44.dp)
        ) {
            Text(requirementText)
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
        Text(connectionState.toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun HomePreview() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(initialHomeState, {}, {}, {}, {}, {}, Modifier.padding(innerPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Home screen", uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun HomePreviewLight() {
    HeartRateMonitorTheme {
        Scaffold { innerPadding ->
            HomeScreen(initialHomeState, {}, {}, {}, {}, {}, Modifier.padding(innerPadding))
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
                    missingRequirements = listOf(
                        Requirement.BLUETOOTH,
                        Requirement.BLUETOOTH_PERMISSION,
                        Requirement.LOCATION
                    )
                ),
                onBluetoothClick = {},
                onLocationClick = {},
                onPermissionsClick = {},
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
                onBluetoothClick = {},
                onLocationClick = {},
                onPermissionsClick = {},
                onConnectClick = {},
                onDisconnectClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

private val DefaultPadding = 12.dp
private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    Manifest.permission.BLUETOOTH_CONNECT
} else {
    Manifest.permission.ACCESS_FINE_LOCATION
}