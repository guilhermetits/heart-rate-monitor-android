package titsch.guilherme.heartratemonitor.central.usecases

import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager

class GetDeviceConnectionStateFlowUseCase(private val centralManager: CentralManager) {
    operator fun invoke() = centralManager.connectionState
}