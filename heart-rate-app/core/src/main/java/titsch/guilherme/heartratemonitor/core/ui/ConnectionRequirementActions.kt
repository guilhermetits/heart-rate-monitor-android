package titsch.guilherme.heartratemonitor.core.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothActivityContract
import titsch.guilherme.heartratemonitor.core.bluetooth.LocationActivityContract
import titsch.guilherme.heartratemonitor.core.model.Requirement
import titschkoski.guilherme.heartratemonitor.core.R

@Composable
fun ConnectionRequirementsActions(
    missingRequirements: List<Requirement>,
    onBluetoothAction: () -> Unit,
    onLocationAction: () -> Unit,
    onPermissionAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcherBluetooth = rememberLauncherForActivityResult(BluetoothActivityContract()) {
        onBluetoothAction()
    }
    val launcherLocation = rememberLauncherForActivityResult(LocationActivityContract()) {
        onLocationAction()
    }
    val launcherPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            onPermissionAction()
        }

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
                onRequirementClick = { launcherBluetooth.launch() }
            )
            RequirementRequest(
                requirementText = stringResource(id = R.string.enable_location),
                isMissing = missingRequirements.contains(Requirement.LOCATION),
                onRequirementClick = { launcherLocation.launch() }
            )
            RequirementRequest(
                requirementText = stringResource(id = R.string.grant_permission),
                isMissing = missingRequirements.contains(Requirement.BLUETOOTH_PERMISSION),
                onRequirementClick = { launcherPermission.launch(permission) }
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(DefaultPadding)
            .fillMaxWidth(),
    ) {
        Icon(
            imageVector = icon.first,
            contentDescription = "",
            tint = icon.second,
            modifier = Modifier
                .padding(DefaultPadding)
        )
        Button(
            onClick = onRequirementClick,
            enabled = isMissing,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
        ) {
            Text(requirementText)
        }
    }
}

private val DefaultPadding = 12.dp
private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    Manifest.permission.BLUETOOTH_CONNECT
} else {
    Manifest.permission.ACCESS_FINE_LOCATION
}