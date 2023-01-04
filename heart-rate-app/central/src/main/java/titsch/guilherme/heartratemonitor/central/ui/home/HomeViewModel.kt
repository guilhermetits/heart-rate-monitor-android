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
import titsch.guilherme.heartratemonitor.central.ui.component.HeartRateMeasurementUIModel
import titsch.guilherme.heartratemonitor.central.ui.component.toUiModel
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.DisconnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.GetBluetoothStateFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.GetDeviceConnectionStateFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.HasBluetoothScanPermissionUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.IsLocationServiceEnabledUseCase
import titsch.guilherme.heartratemonitor.central.usecases.measurements.GetLastHeartRateMeasurementFlowUseCase
import titsch.guilherme.heartratemonitor.core.date.DateProvider
import titsch.guilherme.heartratemonitor.core.model.ConnectionState
import titsch.guilherme.heartratemonitor.core.model.HeartRateMeasurement
import titsch.guilherme.heartratemonitor.core.model.Requirement

class HomeViewModel(
    private val dateProvider: DateProvider,
    private val connectDeviceUseCase: ConnectDeviceUseCase,
    private val disconnectDeviceUseCase: DisconnectDeviceUseCase,
    private val hasBluetoothScanPermissionUseCase: HasBluetoothScanPermissionUseCase,
    private val isLocationServiceEnabledUseCase: IsLocationServiceEnabledUseCase,
    getLastHeartRateMeasurementFlowUseCase: GetLastHeartRateMeasurementFlowUseCase,
    getDeviceConnectionStateFlowUseCase: GetDeviceConnectionStateFlowUseCase,
    getBluetoothStateFlowUseCase: GetBluetoothStateFlowUseCase,
) : ViewModel() {
    private val state = MutableStateFlow(initialHomeState)
    val homeState: StateFlow<HomeState> = state.asStateFlow()

    private var hasBluetoothPermissions = false
    private var locationEnabled = false

    private val lastHeartRateMeasurementStateFlow = MutableStateFlow<HeartRateMeasurement?>(null)

    private val bluetoothStateFlow =
        getBluetoothStateFlowUseCase().onEach { updateRequirementsState(bluetoothEnabled = it) }
            .stateIn(
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
        viewModelScope.launch {
            getLastHeartRateMeasurementFlowUseCase().onEach {
                lastHeartRateMeasurementStateFlow.emit(it)
                updateRequirementsState()
            }.collect()
        }
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
            (connection == ConnectionState.DISCONNECTED
                || connection == ConnectionState.CONNECTING)
                && allRequirementsMatch
        val disconnectEnabled = (connection == ConnectionState.CONNECTED
            || connection == ConnectionState.SCANNING
            ) && allRequirementsMatch
        if (!bluetooth) missingRequirements.add(Requirement.BLUETOOTH)
        if (!locationEnabled) missingRequirements.add(Requirement.LOCATION)
        if (!hasBluetoothPermissions) missingRequirements.add(Requirement.BLUETOOTH_PERMISSION)

        state.update {
            it.copy(
                allRequiredPermissionsGranted = allRequirementsMatch,
                connectEnabled = connectEnabled,
                disconnectEnabled = disconnectEnabled,
                connectionState = connection,
                missingRequirements = missingRequirements,
                heartRateMeasurement = lastHeartRateMeasurementStateFlow.value?.toUiModel(
                    dateProvider
                )
            )
        }
    }
}

data class HomeState(
    val allRequiredPermissionsGranted: Boolean,
    val connectionState: ConnectionState,
    val connectEnabled: Boolean,
    val disconnectEnabled: Boolean,
    val missingRequirements: List<Requirement>,
    val heartRateMeasurement: HeartRateMeasurementUIModel? = null
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
