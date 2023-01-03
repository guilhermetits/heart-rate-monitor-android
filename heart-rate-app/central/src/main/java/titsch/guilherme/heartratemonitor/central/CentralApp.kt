package titsch.guilherme.heartratemonitor.central

import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.BuildConfig
import titsch.guilherme.heartratemonitor.bluetooth.di.centralModule
import titsch.guilherme.heartratemonitor.central.background.CentralService
import titsch.guilherme.heartratemonitor.central.di.appModule
import titsch.guilherme.heartratemonitor.central.di.viewModelModule
import titsch.guilherme.heartratemonitor.core.di.coreModule
import titsch.guilherme.heartratemonitor.core.notification.NotificationManager

class CentralApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@CentralApp)
            allowOverride(true)
            modules(centralModule, coreModule, appModule, viewModelModule)
        }

        createNotificationChannel()

        startForegroundService(CentralService.createIntent(this))
    }

    private fun createNotificationChannel() {
        getKoin().get<NotificationManager>().createNotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            resources.getString(R.string.central_notification_channel_name)
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "HEART_RATE_MONITOR_CENTRAL_NOTIFICATIONS"
    }
}
