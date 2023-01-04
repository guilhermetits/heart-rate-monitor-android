package titsch.guilherme.heartratemonitor.peripheral.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.peripheral.background.PeripheralController
import titsch.guilherme.heartratemonitor.peripheral.ui.home.HomeViewModel
import titsch.guilherme.heartratemonitor.peripheral.usecases.AllowConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.DenyConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.EmitHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GenerateHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GetBluetoothStateFlowUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GetConnectedDevicesFlowUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.HasBluetoothScanPermissionUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.IsDeviceAdvertisingFlowUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.IsLocationServiceEnabledUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StartBluetoothPeripheralUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StopBluetoothPeripheralUseCase

val useCaseModules = module {
    singleOf(::AllowConnectionsUseCase)
    singleOf(::DenyConnectionsUseCase)
    singleOf(::EmitHeartRateMeasurementUseCase)
    singleOf(::GenerateHeartRateMeasurementUseCase)
    singleOf(::GetBluetoothStateFlowUseCase)
    singleOf(::GetConnectedDevicesFlowUseCase)
    singleOf(::HasBluetoothScanPermissionUseCase)
    singleOf(::IsDeviceAdvertisingFlowUseCase)
    singleOf(::IsLocationServiceEnabledUseCase)
    singleOf(::StartBluetoothPeripheralUseCase)
    singleOf(::StopBluetoothPeripheralUseCase)
    singleOf(::PeripheralController)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
}