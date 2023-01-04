package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

class DenyConnectionsUseCase(private val peripheralManager: PeripheralManager) {
    suspend operator fun invoke() {
        if (peripheralManager.isInitialized) {
            peripheralManager.denyNewConnections()
        }
    }
}