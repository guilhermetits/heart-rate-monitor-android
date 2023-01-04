package titsch.guilherme.heartratemonitor.core.di

import android.content.Context
import androidx.room.Room
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothStateObserver
import titsch.guilherme.heartratemonitor.core.date.DateProvider
import titsch.guilherme.heartratemonitor.core.db.HeartRateDatabase
import titsch.guilherme.heartratemonitor.core.db.repositories.HeartRateMeasurementRepository
import titsch.guilherme.heartratemonitor.core.location.LocationService
import titsch.guilherme.heartratemonitor.core.notification.NotificationManager

val coreModule = module {
    singleOf(::LocationService)
    singleOf(::NotificationManager)
    singleOf(::BluetoothStateObserver)
    singleOf(::DateProvider)
    single {
        get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    }
    single {
        get<Context>().getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            HeartRateDatabase::class.java, "heart-rate-database"
        ).build()
    }
    single { get<HeartRateDatabase>().heartRateMeasurementDao() }
    singleOf(::HeartRateMeasurementRepository)
}