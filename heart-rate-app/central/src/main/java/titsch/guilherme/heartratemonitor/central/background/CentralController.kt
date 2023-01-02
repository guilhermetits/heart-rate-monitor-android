package titsch.guilherme.heartratemonitor.central.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.central.usecases.CollectHRMeasurementsUseCase
import titsch.guilherme.heartratemonitor.central.usecases.StartBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.StopBluetoothCentralUseCase

internal class CentralController(
    private val collectHRMeasurementsUseCase: CollectHRMeasurementsUseCase,
    private val startBluetoothCentralUseCase: StartBluetoothCentralUseCase,
    private val stopBluetoothCentralUseCase: StopBluetoothCentralUseCase,
) {
    private var coroutineScope: CoroutineScope? = null

    fun start() {
        if (coroutineScope != null) return
        CoroutineScope(Dispatchers.Default + Job()).also {
            it.launch {
                startBluetoothCentralUseCase(false)
                collectHRMeasurementsUseCase()
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