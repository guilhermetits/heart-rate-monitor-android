package titsch.guilherme.heartratemonitor.central.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.StartBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.StopBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.measurements.CollectHeartRateMeasurementsUseCase

internal class CentralController(
    private val collectHeartRateMeasurementsUseCase: CollectHeartRateMeasurementsUseCase,
    private val startBluetoothCentralUseCase: StartBluetoothCentralUseCase,
    private val stopBluetoothCentralUseCase: StopBluetoothCentralUseCase,
) {
    private var coroutineScope: CoroutineScope? = null

    fun start() {
        if (coroutineScope != null) return
        CoroutineScope(Dispatchers.Default + Job()).also {
            it.launch {
                startBluetoothCentralUseCase(false)
                collectHeartRateMeasurementsUseCase()
            }
            coroutineScope = it
        }
    }

    fun stop() {
        coroutineScope?.launch { stopBluetoothCentralUseCase() }
        coroutineScope?.cancel()
        coroutineScope = null
    }
}