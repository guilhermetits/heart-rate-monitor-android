package titsch.guilherme.heartratemonitor.central.usecases

import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager

internal class StopBluetoothCentralUseCase(private val centralManager: CentralManager) {
    suspend operator fun invoke() {
        centralManager.stop()
    }
}