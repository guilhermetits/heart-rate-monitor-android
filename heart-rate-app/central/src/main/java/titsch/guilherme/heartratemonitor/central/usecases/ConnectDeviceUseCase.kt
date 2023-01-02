package titsch.guilherme.heartratemonitor.central.usecases

import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager

internal class ConnectDeviceUseCase(private val centralManager: CentralManager) {

    suspend operator fun invoke() {
        if (centralManager.isInitialized) {
            centralManager.connect()
        }
    }
}