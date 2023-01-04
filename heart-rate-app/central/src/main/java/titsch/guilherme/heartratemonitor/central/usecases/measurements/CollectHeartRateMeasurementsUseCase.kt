package titsch.guilherme.heartratemonitor.central.usecases.measurements

import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager
import titsch.guilherme.heartratemonitor.core.date.DateProvider
import titsch.guilherme.heartratemonitor.core.date.toLocalDateTime
import titsch.guilherme.heartratemonitor.core.db.repositories.HeartRateMeasurementRepository
import titsch.guilherme.heartratemonitor.core.model.HeartRateMeasurement

internal class CollectHeartRateMeasurementsUseCase(
    private val heartRateMeasurementRepository: HeartRateMeasurementRepository,
    private val centralManager: CentralManager,
    private val dateProvider: DateProvider
) {
    suspend operator fun invoke() {
        if (centralManager.isInitialized) {
            centralManager.heartRateFlow.collect { heartRate ->
                HeartRateMeasurement(0, heartRate, dateProvider.now()).also {
                    heartRateMeasurementRepository.insertOrUpdate(measurement = it)
                    Timber.i(
                        "New heart rate received at with value ${it.value} at ${
                            it.date.toLocalDateTime(
                                dateProvider
                            )
                        }"
                    )

                }
            }
        }
    }
}