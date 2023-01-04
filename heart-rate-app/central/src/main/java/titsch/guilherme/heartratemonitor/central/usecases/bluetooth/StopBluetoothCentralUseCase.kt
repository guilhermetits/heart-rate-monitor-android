package titsch.guilherme.heartratemonitor.central.usecases.bluetooth

import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager

internal class StopBluetoothCentralUseCase(private val centralManager: CentralManager) {
    suspend operator fun invoke() {
        centralManager.stop()
    }
}