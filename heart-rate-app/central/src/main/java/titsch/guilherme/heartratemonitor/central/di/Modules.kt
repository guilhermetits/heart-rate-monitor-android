package titsch.guilherme.heartratemonitor.central.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.central.usecases.CollectHRMeasurementsUseCase
import titsch.guilherme.heartratemonitor.central.usecases.StartBluetoothCentralUseCase
import titsch.guilherme.heartratemonitor.central.usecases.StopBluetoothCentralUseCase

val useCaseModule = module {

    singleOf(::CollectHRMeasurementsUseCase)
    singleOf(::StartBluetoothCentralUseCase)
    singleOf(::StopBluetoothCentralUseCase)

}