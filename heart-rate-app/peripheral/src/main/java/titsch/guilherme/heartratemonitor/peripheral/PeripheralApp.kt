package titsch.guilherme.heartratemonitor.peripheral

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.BuildConfig
import titsch.guilherme.heartratemonitor.bluetooth.di.peripheralModule
import titsch.guilherme.heartratemonitor.core.di.coreModule

class PeripheralApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@PeripheralApp)
            allowOverride(true)
            modules(peripheralModule, coreModule)
        }
    }
}
