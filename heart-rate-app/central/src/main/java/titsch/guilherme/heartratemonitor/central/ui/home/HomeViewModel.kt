package titsch.guilherme.heartratemonitor.central.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.central.usecases.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.DisconnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.GetBluetoothStateUseCase
import titsch.guilherme.heartratemonitor.central.usecases.GetDeviceConnectionStateFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.HasBluetoothScanPermissionUseCase
import titsch.guilherme.heartratemonitor.central.usecases.IsLocationServiceEnabledUseCase
import titsch.guilherme.heartratemonitor.core.model.ConnectionState

class HomeViewModel(
    private val connectDeviceUseCase: ConnectDeviceUseCase,
    private val disconnectDeviceUseCase: DisconnectDeviceUseCase,
    private val hasBluetoothScanPermissionUseCase: HasBluetoothScanPermissionUseCase,
    private val isLocationServiceEnabledUseCase: IsLocationServiceEnabledUseCase,
    getDeviceConnectionStateFlowUseCase: GetDeviceConnectionStateFlowUseCase,
    getBluetoothStateUseCase: GetBluetoothStateUseCase,
) : ViewModel() {
    private val state = MutableStateFlow(initialHomeState)
    val homeState: StateFlow<HomeState> = state.asStateFlow()

    private var hasBluetoothPermissions = false
    private var locationEnabled = false

    private val bluetoothStateFlow =
        getBluetoothStateUseCase().onEach { updateRequirementsState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = false
        )

    private val connectionStateFlow =
        getDeviceConnectionStateFlowUseCase().onEach { updateRequirementsState(connState = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = ConnectionState.DISCONNECTED
            )

    init {
        viewModelScope.launch {
            bluetoothStateFlow.collect()
        }
        viewModelScope.launch { connectionStateFlow.collect() }
        refreshRequirements()
    }

    fun connect() = viewModelScope.launch {
        connectDeviceUseCase()
    }

    fun disconnect() = viewModelScope.launch {
        disconnectDeviceUseCase()
    }

    fun refreshRequirements() = viewModelScope.launch {
        locationEnabled = isLocationServiceEnabledUseCase()
        hasBluetoothPermissions = hasBluetoothScanPermissionUseCase()
        updateRequirementsState()
    }

    private fun updateRequirementsState(
        bluetoothEnabled: Boolean? = null, connState: ConnectionState? = null
    ) {
        val missingRequirements = mutableListOf<Requirement>()
        val connection = connState ?: connectionStateFlow.value
        val bluetooth = bluetoothEnabled ?: bluetoothStateFlow.value
        val allRequirementsMatch = bluetooth && locationEnabled && hasBluetoothPermissions
        val connectEnabled =
            (connection == ConnectionState.DISCONNECTED || connection == ConnectionState.CONNECTING)
                && allRequirementsMatch
        val disconnectEnabled = connection == ConnectionState.CONNECTED && allRequirementsMatch
        if (!bluetooth) missingRequirements.add(Requirement.BLUETOOTH)
        if (!locationEnabled) missingRequirements.add(Requirement.LOCATION)
        if (!hasBluetoothPermissions) missingRequirements.add(Requirement.BLUETOOTH_PERMISSION)

        state.update {
            it.copy(
                allRequiredPermissionsGranted = allRequirementsMatch,
                connectEnabled = connectEnabled,
                disconnectEnabled = disconnectEnabled,
                connectionState = connection,
                missingRequirements = missingRequirements
            )
        }
    }
}

data class HomeState(
    val allRequiredPermissionsGranted: Boolean,
    val connectionState: ConnectionState,
    val connectEnabled: Boolean,
    val disconnectEnabled: Boolean,
    val missingRequirements: List<Requirement>
)

val initialHomeState = HomeState(
    allRequiredPermissionsGranted = false,
    connectionState = ConnectionState.DISCONNECTED,
    connectEnabled = false,
    disconnectEnabled = false,
    missingRequirements = listOf(
        Requirement.BLUETOOTH, Requirement.LOCATION, Requirement.BLUETOOTH_PERMISSION
    )
)

enum class Requirement {
    BLUETOOTH, LOCATION, BLUETOOTH_PERMISSION
}