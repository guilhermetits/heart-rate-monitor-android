package titsch.guilherme.heartratemonitor.core.db.converters

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {

    @TypeConverter
    fun fromInstant(date: Instant): Long = date.epochSecond

    @TypeConverter
    fun toInstant(date: Long): Instant = Instant.ofEpochSecond(date)
}