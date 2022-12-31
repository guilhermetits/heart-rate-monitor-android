package titsch.guilherme.heartratemonitor.central

import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.BuildConfig

class CentralApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
