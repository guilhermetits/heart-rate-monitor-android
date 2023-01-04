package titsch.guilherme.heartratemonitor.core.db.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import titsch.guilherme.heartratemonitor.core.db.dao.HeartRateMeasurementDao
import titsch.guilherme.heartratemonitor.core.db.entities.HeartRateMeasurementEntity
import titsch.guilherme.heartratemonitor.core.model.HeartRateMeasurement

class HeartRateMeasurementRepository(
    private val dao: HeartRateMeasurementDao
) {
    suspend fun allMeasurementsFlowList(): Flow<List<HeartRateMeasurement>> =
        withContext(Dispatchers.IO) {
            dao.allCMeasurementsDesc().map { list ->
                list.map { it.toHeartRateMeasurement() }
            }
        }

    suspend fun lastMeasurementFlow(): Flow<HeartRateMeasurement?> =
        withContext(Dispatchers.IO) {
            dao.lastMeasurement().map {
                it?.toHeartRateMeasurement()
            }
        }

    suspend fun insertOrUpdate(measurement: HeartRateMeasurement) {
        withContext(Dispatchers.IO) {
            dao.insertOrUpdate(
                measurement.toHeartRateMeasurementEntity()
            )
        }
    }

    private fun HeartRateMeasurement.toHeartRateMeasurementEntity() =
        HeartRateMeasurementEntity(this.id, this.value, this.date)

    private fun HeartRateMeasurementEntity.toHeartRateMeasurement() =
        HeartRateMeasurement(this.id, this.value, this.date)
}