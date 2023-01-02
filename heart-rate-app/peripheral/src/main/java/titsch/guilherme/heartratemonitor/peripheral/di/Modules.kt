package titsch.guilherme.heartratemonitor.peripheral.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.peripheral.background.PeripheralController
import titsch.guilherme.heartratemonitor.peripheral.usecases.AllowConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.DenyConnectionsUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.EmitHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.GenerateHeartRateMeasurementUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StartBluetoothPeripheralUseCase
import titsch.guilherme.heartratemonitor.peripheral.usecases.StopBluetoothPeripheralUseCase

val appModule = module {
    singleOf(::AllowConnectionsUseCase)
    singleOf(::DenyConnectionsUseCase)
    singleOf(::EmitHeartRateMeasurementUseCase)
    singleOf(::GenerateHeartRateMeasurementUseCase)
    singleOf(::StartBluetoothPeripheralUseCase)
    singleOf(::StopBluetoothPeripheralUseCase)
    singleOf(::PeripheralController)

}