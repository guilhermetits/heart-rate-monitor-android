package titsch.guilherme.heartratemonitor.peripheral

import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.BuildConfig

class PeripheralApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
