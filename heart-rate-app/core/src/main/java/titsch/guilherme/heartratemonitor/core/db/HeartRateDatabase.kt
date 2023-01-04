package titsch.guilherme.heartratemonitor.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import titsch.guilherme.heartratemonitor.core.db.converters.InstantConverter
import titsch.guilherme.heartratemonitor.core.db.dao.HeartRateMeasurementDao
import titsch.guilherme.heartratemonitor.core.db.entities.HeartRateMeasurementEntity

@Database(entities = [HeartRateMeasurementEntity::class], version = 1)
@TypeConverters(InstantConverter::class)
abstract class HeartRateDatabase : RoomDatabase() {
    abstract fun heartRateMeasurementDao(): HeartRateMeasurementDao
}
