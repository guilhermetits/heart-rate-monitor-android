package titsch.guilherme.heartratemonitor.central.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.central.background.CentralController
import titsch.guilherme.heartratemonitor.central.ui.home.HomeViewModel
import titsch.guilherme.heartratemonitor.central.usecases.CollectHRMeasurementsUseCase
import titsch.guilherme.heartratemonitor.central.usecases.ConnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.DisconnectDeviceUseCase
import titsch.guilherme.heartratemonitor.central.usecases.GetBluetoothStateUseCase
import titsch.guilherme.heartratemonitor.central.usecases.GetDeviceConnectionStateFlowUseCase
import titsch.guilherme.heartratemonitor.central.usecases.HasBluetoothScanPermissionUseCase
import titsch.guilherme.heartratemonitor.central.usecases.IsLocationServiceEnabledUseCase
import titsch.guilherme.heartratemonitor.central.usecases.StartBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.StopBluetoothCentralUseCase

val appModule = module {
    singleOf(::CollectHRMeasurementsUseCase)
    singleOf(::ConnectDeviceUseCase)
    singleOf(::DisconnectDeviceUseCase)
    singleOf(::GetBluetoothStateUseCase)
    singleOf(::GetDeviceConnectionStateFlowUseCase)
    singleOf(::HasBluetoothScanPermissionUseCase)
    singleOf(::IsLocationServiceEnabledUseCase)
    singleOf(::StartBluetoothCentralUseCase)
    singleOf(::StopBluetoothCentralUseCase)
    singleOf(::CentralController)
}
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
}