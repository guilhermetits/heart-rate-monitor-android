package titsch.guilherme.heartratemonitor.bluetooth.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.bluetooth.central.CentralManager
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateClient
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateScanner
import titsch.guilherme.heartratemonitor.bluetooth.peripheral.PeripheralManager
import titsch.guilherme.heartratemonitor.bluetooth.peripheral.server.ConnectionManagerFactory
import titsch.guilherme.heartratemonitor.bluetooth.peripheral.server.HeartRateServer

val centralModule = module {
    singleOf(::CentralManager)
    singleOf(::HeartRateScanner)
    singleOf(::HeartRateClient)
    single<BluetoothAdapter> {
        (get<Context>().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
}

val peripheralModule = module {
    singleOf(::PeripheralManager)
    singleOf(::ConnectionManagerFactory)
    singleOf(::HeartRateServer)
    single<BluetoothAdapter> {
        (get<Context>().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
}