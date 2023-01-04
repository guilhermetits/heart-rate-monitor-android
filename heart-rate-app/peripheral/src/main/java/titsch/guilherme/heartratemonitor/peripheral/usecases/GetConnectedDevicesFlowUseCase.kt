package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

class GetConnectedDevicesFlowUseCase(private val peripheralManager: PeripheralManager) {
    operator fun invoke() = peripheralManager.connectedDevicesFlow
}