package titsch.guilherme.heartratemonitor.core.date

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class DateProvider {
    fun now(): Instant = Instant.now()

    fun getZoneOffset(): ZoneOffset {
        return ZoneId
            .systemDefault().rules
            .getOffset(Instant.now())
    }
}