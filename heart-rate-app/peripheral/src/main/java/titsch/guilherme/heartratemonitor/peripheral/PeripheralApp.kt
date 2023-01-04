package titsch.guilherme.heartratemonitor.peripheral

import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.BuildConfig
import titsch.guilherme.heartratemonitor.bluetooth.di.peripheralModule
import titsch.guilherme.heartratemonitor.core.di.coreModule
import titsch.guilherme.heartratemonitor.core.notification.NotificationManager
import titsch.guilherme.heartratemonitor.peripheral.background.PeripheralService
import titsch.guilherme.heartratemonitor.peripheral.di.useCaseModules
import titsch.guilherme.heartratemonitor.peripheral.di.viewModelModule

class PeripheralApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@PeripheralApp)
            allowOverride(true)
            modules(useCaseModules, peripheralModule, coreModule, viewModelModule)
        }

        createNotificationChannel()

        startForegroundService(PeripheralService.createIntent(this))
    }

    private fun createNotificationChannel() {
        getKoin().get<NotificationManager>().createNotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            resources.getString(R.string.central_notification_channel_name)
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "PERIPHERAL_NOTIFICATION_CHANNEL_ID"
    }
}
