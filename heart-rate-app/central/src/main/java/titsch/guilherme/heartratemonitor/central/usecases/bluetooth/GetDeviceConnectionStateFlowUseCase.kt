package titsch.guilherme.heartratemonitor.central.usecases.bluetooth

import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager

class GetDeviceConnectionStateFlowUseCase(private val centralManager: CentralManager) {
    operator fun invoke() = centralManager.connectionState
}