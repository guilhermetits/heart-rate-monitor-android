package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

class StartBluetoothPeripheralUseCase(private val peripheralManager: PeripheralManager) {
    suspend operator fun invoke(openConnection: Boolean) {
        peripheralManager.start(openConnection)
    }
}