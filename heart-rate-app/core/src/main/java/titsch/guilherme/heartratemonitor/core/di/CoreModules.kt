package titsch.guilherme.heartratemonitor.core.di

import android.content.Context
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothStateObserver
import titsch.guilherme.heartratemonitor.core.location.LocationService
import titsch.guilherme.heartratemonitor.core.notification.NotificationManager

var coreModule = module {
    singleOf(::LocationService)
    singleOf(::NotificationManager)
    singleOf(::BluetoothStateObserver)
    single {
        get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    }
    single {
        get<Context>().getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    }
}