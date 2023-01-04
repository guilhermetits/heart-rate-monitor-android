package titsch.guilherme.heartratemonitor.peripheral.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.core.model.Requirement
import titsch.guilherme.heartratemonitor.peripheral.usecases.AllowConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.DenyConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GetBluetoothStateFlowUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GetConnectedDevicesFlowUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.HasBluetoothScanPermissionUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.IsDeviceAdvertisingFlowUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.IsLocationServiceEnabledUseCase

class HomeViewModel(
    private val allowConnectionsUseCase: AllowConnectionsUseCase,
    private val denyConnectionsUseCase: DenyConnectionsUseCase,
    private val hasBluetoothScanPermissionUseCase: HasBluetoothScanPermissionUseCase,
    private val isLocationServiceEnabledUseCase: IsLocationServiceEnabledUseCase,
    isDeviceAdvertisingFlowUseCase: IsDeviceAdvertisingFlowUseCase,
    getConnectedDevicesFlowUseCase: GetConnectedDevicesFlowUseCase,
    getBluetoothStateFlowUseCase: GetBluetoothStateFlowUseCase,
) : ViewModel() {
    private val state = MutableStateFlow(initialHomeState)
    val homeState: StateFlow<HomeState> = state.asStateFlow()

    private var hasBluetoothPermissions = false
    private var locationEnabled = false

    private val bluetoothStateFlow =
        getBluetoothStateFlowUseCase().onEach { updateRequirementsState(bluetoothEnabled = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = false
            )

    private val advertisingStateFlow =
        isDeviceAdvertisingFlowUseCase().onEach { updateRequirementsState(isAdvertising = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = false
            )
    private val connectedDevicesStateFlow =
        getConnectedDevicesFlowUseCase()
            .map { it.count() }
            .onEach { updateRequirementsState(connectedDevices = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = 0
            )

    init {
        viewModelScope.launch { bluetoothStateFlow.collect() }
        viewModelScope.launch { connectedDevicesStateFlow.collect() }
        viewModelScope.launch { advertisingStateFlow.collect() }
        refreshRequirements()
    }

    fun allowConnections() = viewModelScope.launch {
        allowConnectionsUseCase()
    }

    fun denyConnections() = viewModelScope.launch {
        denyConnectionsUseCase()
    }

    fun refreshRequirements() = viewModelScope.launch {
        locationEnabled = isLocationServiceEnabledUseCase()
        hasBluetoothPermissions = hasBluetoothScanPermissionUseCase()
        updateRequirementsState()
    }

    private fun updateRequirementsState(
        bluetoothEnabled: Boolean? = null,
        connectedDevices: Int? = null,
        isAdvertising: Boolean? = null
    ) {
        val advertising = isAdvertising ?: advertisingStateFlow.value
        val devices = connectedDevices ?: connectedDevicesStateFlow.value
        val missingRequirements = mutableListOf<Requirement>()
        val bluetooth = bluetoothEnabled ?: bluetoothStateFlow.value
        val allRequirementsMatch = bluetooth && locationEnabled && hasBluetoothPermissions
        if (!bluetooth) missingRequirements.add(Requirement.BLUETOOTH)
        if (!locationEnabled) missingRequirements.add(Requirement.LOCATION)
        if (!hasBluetoothPermissions) missingRequirements.add(Requirement.BLUETOOTH_PERMISSION)

        state.update {
            it.copy(
                allRequiredPermissionsGranted = allRequirementsMatch,
                allowConnectionsEnabled = !advertising,
                denyConnectionsEnabled = advertising,
                connectedDevicesCount = devices,
                missingRequirements = missingRequirements
            )
        }
    }
}

data class HomeState(
    val allRequiredPermissionsGranted: Boolean,
    val connectedDevicesCount: Int,
    val allowConnectionsEnabled: Boolean,
    val denyConnectionsEnabled: Boolean,
    val missingRequirements: List<Requirement>
)

val initialHomeState = HomeState(
    allRequiredPermissionsGranted = false,
    connectedDevicesCount = 0,
    allowConnectionsEnabled = false,
    denyConnectionsEnabled = false,
    missingRequirements = listOf(
        Requirement.BLUETOOTH, Requirement.LOCATION, Requirement.BLUETOOTH_PERMISSION
    )
)
