package titsch.guilherme.heartratemonitor.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import titsch.guilherme.heartratemonitor.core.db.entities.HeartRateMeasurementEntity

@Dao
interface HeartRateMeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(obj: HeartRateMeasurementEntity): Long

    @Query("SELECT * FROM heart_rate_measurements ORDER BY date DESC")
    fun allCMeasurementsDesc(): Flow<List<HeartRateMeasurementEntity>>

    @Query("SELECT * FROM heart_rate_measurements ORDER BY date DESC LIMIT 1")
    fun lastMeasurement(): Flow<HeartRateMeasurementEntity?>
}