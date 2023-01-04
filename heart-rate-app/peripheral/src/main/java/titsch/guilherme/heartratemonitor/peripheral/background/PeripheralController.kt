package titsch.guilherme.heartratemonitor.peripheral.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import titsch.guilherme.heartratemonitor.peripheral.usecases.EmitHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GenerateHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StartBluetoothPeripheralUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StopBluetoothPeripheralUseCase

internal class PeripheralController(
    private val startBluetoothPeripheralUseCase: StartBluetoothPeripheralUseCase,
    private val stopBluetoothPeripheralUseCase: StopBluetoothPeripheralUseCase,
    private val generateHeartRateMeasurementUseCase: GenerateHeartRateMeasurementUseCase,
    private val emitHeartRateMeasurementUseCase: EmitHeartRateMeasurementUseCase
) {
    private val emissionScope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private var emissionJob: Job? = null

    suspend fun start() {
        startBluetoothPeripheralUseCase(false)
            .also {
                emissionJob = generateHeartRateMeasurementUseCase(interval = 10000)
                    .cancellable()
                    .onEach { heartRate ->
                        emitHeartRateMeasurementUseCase(heartRate)
                    }.launchIn(emissionScope)

            }
    }

    suspend fun stop() {
        stopBluetoothPeripheralUseCase()
        emissionJob?.cancel()
        emissionJob = null
    }
}