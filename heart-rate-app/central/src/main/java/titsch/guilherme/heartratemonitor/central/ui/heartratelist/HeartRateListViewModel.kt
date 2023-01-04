package titsch.guilherme.heartratemonitor.central.ui.heartratelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import titsch.guilherme.heartratemonitor.central.ui.component.HeartRateMeasurementUIModel
import titsch.guilherme.heartratemonitor.central.ui.component.toUiModel
import titsch.guilherme.heartratemonitor.central.usecases.measurements.GetAllHeartRateMeasurementFlowUseCase
import titsch.guilherme.heartratemonitor.core.date.DateProvider

class HeartRateListViewModel(
    dateProvider: DateProvider,
    getAllHeartRateMeasurementFlowUseCase: GetAllHeartRateMeasurementFlowUseCase
) : ViewModel() {

    private val _heartRateListFlow = MutableSharedFlow<List<HeartRateMeasurementUIModel>>()
    val heartRateListFlow: StateFlow<List<HeartRateMeasurementUIModel>> =
        _heartRateListFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = listOf()
        )

    init {
        viewModelScope.launch {
            getAllHeartRateMeasurementFlowUseCase().map { list ->
                list.map {
                    it.toUiModel(
                        dateProvider
                    )
                }
            }.onEach {
                _heartRateListFlow.emit(it)
            }.collect()
        }
    }
}