package titsch.guilherme.heartratemonitor.central.usecases.bluetooth

import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager

internal class StartBluetoothCentralUseCase(private val centralManager: CentralManager) {
    suspend operator fun invoke(openConnection: Boolean) {
        centralManager.start(openConnection)
    }
}