package titsch.guilherme.heartratemonitor.core.date

import android.text.format.DateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.toLocalDateTime(dateProvider: DateProvider): LocalDateTime =
    LocalDateTime.ofInstant(this, dateProvider.getZoneOffset())

fun LocalDateTime.formatLong(): String {
    val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMM d, yyyy, HH:mm")
    return this.format(DateTimeFormatter.ofPattern(pattern))
}
