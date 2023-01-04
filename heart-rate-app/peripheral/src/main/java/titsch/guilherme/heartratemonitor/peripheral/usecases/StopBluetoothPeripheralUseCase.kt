package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

internal class StopBluetoothPeripheralUseCase(private val peripheralManager: PeripheralManager) {
    suspend operator fun invoke() {
        peripheralManager.stop()
    }
}