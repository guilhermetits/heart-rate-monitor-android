package titsch.guilherme.heartratemonitor.core.model

import java.time.Instant

data class HeartRateMeasurement(val id: Int, val value: Int, val date: Instant)