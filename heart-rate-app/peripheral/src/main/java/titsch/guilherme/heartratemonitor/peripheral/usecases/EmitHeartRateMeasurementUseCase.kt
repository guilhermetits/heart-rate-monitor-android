package titsch.guilherme.heartratemonitor.peripheral.usecases

import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager

internal class EmitHeartRateMeasurementUseCase(private val peripheralManager: PeripheralManager) {
    operator fun invoke(value: Int) {
        if (peripheralManager.isInitialized) {
            peripheralManager.emitHeartRate(value)
        }
    }
}