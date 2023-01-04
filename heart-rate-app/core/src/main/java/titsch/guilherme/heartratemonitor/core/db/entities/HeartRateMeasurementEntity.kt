package titsch.guilherme.heartratemonitor.core.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "heart_rate_measurements")
data class HeartRateMeasurementEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo val id: Int,
    @ColumnInfo val value: Int,
    @ColumnInfo val date: Instant
)