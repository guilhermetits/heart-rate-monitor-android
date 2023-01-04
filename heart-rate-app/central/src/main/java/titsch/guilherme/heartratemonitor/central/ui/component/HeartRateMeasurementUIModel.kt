package titsch.guilherme.heartratemonitor.central.ui.component

import titsch.guilherme.heartratemonitor.core.date.DateProvider
import titsch.guilherme.heartratemonitor.core.date.formatLong
import titsch.guilherme.heartratemonitor.core.date.toLocalDateTime
import titsch.guilherme.heartratemonitor.core.model.HeartRateMeasurement
import java.util.Locale

data class HeartRateMeasurementUIModel(val id: Int, val value: String, val date: String)

fun HeartRateMeasurement.toUiModel(dateProvider: DateProvider) =
    HeartRateMeasurementUIModel(
        id = this.id,
        value = String.format(Locale.getDefault(), "%d bpm", this.value),
        date = this.date.toLocalDateTime(dateProvider).formatLong()
    )