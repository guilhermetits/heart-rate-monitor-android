package titsch.guilherme.heartratemonitor.central.usecases

import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager
import titsch.guilherme.heartratemonitor.central.model.HRMeasurement
import java.time.LocalDateTime

class CollectHRMeasurementsUseCase(private val centralManager: CentralManager) {
    suspend operator fun invoke() {
        if (centralManager.isInitialized) {
            centralManager.heartRateFlow.collect { heartRate ->
                HRMeasurement(heartRate, LocalDateTime.now()).also {
                    Timber.i("New heart rate received at with value ${it.value} at ${it.dateTime}")
                }
            }
        }
    }
}