package titsch.guilherme.heartratemonitor.peripheral.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.peripheral.usecases.GetConnectedDevicesFlowUseCase

class DevicesListViewModel(
    getConnectedDevicesFlowUseCase: GetConnectedDevicesFlowUseCase
) : ViewModel() {

    val connectedDevicesListStateFlow = getConnectedDevicesFlowUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = listOf()
    )

    init {
        viewModelScope.launch { connectedDevicesListStateFlow.collect() }
    }
}