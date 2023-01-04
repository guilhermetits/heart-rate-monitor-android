package titsch.guilherme.heartratemonitor.peripheral.usecases

import kotlinx.coroutines.flow.distinctUntilChanged
import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

class IsDeviceAdvertisingFlowUseCase(private val peripheralManager: PeripheralManager) {
    operator fun invoke() = peripheralManager.advertisementStateFlow.distinctUntilChanged()
}