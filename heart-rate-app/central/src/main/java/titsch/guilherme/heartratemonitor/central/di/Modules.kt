package titsch.guilherme.heartratemonitor.central.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.central.background.CentralController
import titsch.guilherme.heartratemonitor.central.ui.heartratelist.HeartRateListViewModel
import titsch.guilherme.heartratemonitor.central.ui.home.HomeViewModel
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.DisconnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.GetBluetoothStateFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.GetDeviceConnectionStateFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.HasBluetoothScanPermissionUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.IsLocationServiceEnabledUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.StartBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.bluetooth.StopBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.measurements.CollectHeartRateMeasurementsUseCase
import titsch.guilherme.heartratemonitor.central.usecases.measurements.GetAllHeartRateMeasurementFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.measurements.GetLastHeartRateMeasurementFlowUseCase

val useCasesModule = module {
    singleOf(::CollectHeartRateMeasurementsUseCase)
    singleOf(::GetAllHeartRateMeasurementFlowUseCase)
    singleOf(::GetLastHeartRateMeasurementFlowUseCase)
    singleOf(::ConnectDeviceUseCase)
    singleOf(::DisconnectDeviceUseCase)
    singleOf(::GetBluetoothStateFlowUseCase)
    singleOf(::GetDeviceConnectionStateFlowUseCase)
    singleOf(::HasBluetoothScanPermissionUseCase)
    singleOf(::IsLocationServiceEnabledUseCase)
    singleOf(::StartBluetoothCentralUseCase)
    singleOf(::StopBluetoothCentralUseCase)
    singleOf(::CentralController)
}
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::HeartRateListViewModel)
}