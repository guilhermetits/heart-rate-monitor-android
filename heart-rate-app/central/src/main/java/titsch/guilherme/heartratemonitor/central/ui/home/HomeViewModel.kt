package titsch.guilherme.heartratemonitor.central.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.central.usecases.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.DisconnectDeviceUseCase
import titsch.guilherme.heartratemonitor.core.model.ConnectionState

class HomeViewModel(
    private val connectDeviceUseCase: ConnectDeviceUseCase,
    private val disconnectDeviceUseCase: DisconnectDeviceUseCase,
) : ViewModel() {
    fun connect() = viewModelScope.launch {
        connectDeviceUseCase()
    }

    fun disconnect() = viewModelScope.launch {
        disconnectDeviceUseCase()
    }

    fun refreshPermissions() {
    }

    private val state = MutableStateFlow(initialHomeState)
    val homeState: StateFlow<HomeState> = state.asStateFlow()
}

data class HomeState(
    val allRequiredPermissionsGranted: Boolean,
    val connectionState: ConnectionState,
    val connectEnabled: Boolean,
    val disconnectEnabled: Boolean
)

val initialHomeState = HomeState(
    allRequiredPermissionsGranted = false,
    connectionState = ConnectionState.DISCONNECTED,
    connectEnabled = false,
    disconnectEnabled = false
)