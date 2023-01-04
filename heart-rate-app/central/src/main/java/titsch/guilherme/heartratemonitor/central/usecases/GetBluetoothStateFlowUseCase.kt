package titsch.guilherme.heartratemonitor.central.usecases

import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothStateObserver

class GetBluetoothStateFlowUseCase(private val bluetoothStateObserver: BluetoothStateObserver) {
    operator fun invoke() = bluetoothStateObserver()
}