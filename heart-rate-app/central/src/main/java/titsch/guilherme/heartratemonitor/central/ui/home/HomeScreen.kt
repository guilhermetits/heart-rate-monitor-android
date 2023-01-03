package titsch.guilherme.heartratemonitor.central.ui.home

import android.content.res.Configuration
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
import titsch.guilherme.heartratemonitor.core.model.ConnectionState
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme

@Composable
fun HomeScreenRoute(
    viewModel: HomeViewModel = koinViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val homeState by viewModel.homeState.collectAsState()
    HomeScreen(
        homeState = homeState,
        grantPermissionsClick = {},
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
    grantPermissionsClick: () -> Unit,
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
        PermissionsRow(homeState.allRequiredPermissionsGranted, grantPermissionsClick)
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
    permissionsGranted: Boolean,
    onGrantPermissionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (permissionsGranted)
        Icons.Default.Check to MaterialTheme.colorScheme.tertiary
    else
        Icons.Default.Warning to MaterialTheme.colorScheme.error
    val text =
        if (permissionsGranted)
            R.string.permisions_granted
        else
            R.string.missing_permissions

    Column(
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = icon.first,
                contentDescription = "",
                tint = icon.second,
                modifier = Modifier.padding(
                    DefaultPadding
                )
            )
            Text(
                text = stringResource(id = text),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(bottom = DefaultPadding)
            )
        }
        Button(
            onClick = onGrantPermissionsClick,
            enabled = !permissionsGranted,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.grant_permissions),
            )
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
    disconnectEnabled: Boolean,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier
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
                    disconnectEnabled = true
                ),
                grantPermissionsClick = {},
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
                    disconnectEnabled = false
                ),
                grantPermissionsClick = {},
                onConnectClick = {},
                onDisconnectClick = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

private val DefaultPadding = 12.dp