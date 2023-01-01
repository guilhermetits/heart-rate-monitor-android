package titsch.guilherme.heartratemonitor.central.model

import java.time.LocalDateTime

data class HRMeasurement(val value: Int, val dateTime: LocalDateTime)