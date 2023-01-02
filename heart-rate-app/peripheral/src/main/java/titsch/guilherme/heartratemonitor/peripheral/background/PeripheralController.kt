package titsch.guilherme.heartratemonitor.peripheral.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import titsch.guilherme.heartratemonitor.peripheral.usecases.EmitHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GenerateHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StartBluetoothPeripheralUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StopBluetoothPeripheralUseCase

class PeripheralController(
    private val startBluetoothPeripheralUseCase: StartBluetoothPeripheralUseCase,
    private val stopBluetoothPeripheralUseCase: StopBluetoothPeripheralUseCase,
    private val generateHeartRateMeasurementUseCase: GenerateHeartRateMeasurementUseCase,
    private val emitHeartRateMeasurementUseCase: EmitHeartRateMeasurementUseCase
) {
    private var emissionScope: CoroutineScope? = null

    suspend fun start() {
        startBluetoothPeripheralUseCase(false)
        CoroutineScope(Dispatchers.Default + Job()).also {
            generateHeartRateMeasurementUseCase(interval = 10000).cancellable().onEach {
                emitHeartRateMeasurementUseCase(it)
            }.launchIn(it)
            emissionScope = it
        }
    }

    fun stop() {
        stopBluetoothPeripheralUseCase()
        emissionScope?.cancel()
        emissionScope = null
    }
}