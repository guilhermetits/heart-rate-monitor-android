package titsch.guilherme.heartratemonitor.central.usecases.measurements

import titsch.guilherme.heartratemonitor.core.db.repositories.HeartRateMeasurementRepository

class GetAllHeartRateMeasurementFlowUseCase(
    private val heartRateMeasurementRepository: HeartRateMeasurementRepository
) {
    suspend operator fun invoke() = heartRateMeasurementRepository.allMeasurementsFlowList()
}