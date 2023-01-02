package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

class AllowConnectionsUseCase(private val peripheralManager: PeripheralManager) {
    operator fun invoke() {
        peripheralManager.allowNewConnections()
    }
}