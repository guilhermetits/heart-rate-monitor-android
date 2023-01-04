package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

class RestartBluetoothPeripheralUseCase(private val peripheralManager: PeripheralManager) {
    suspend operator fun invoke() {
        peripheralManager.restart()
    }
}