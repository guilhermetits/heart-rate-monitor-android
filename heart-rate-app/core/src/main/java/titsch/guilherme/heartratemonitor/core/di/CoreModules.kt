package titsch.guilherme.heartratemonitor.core.di

import android.content.Context
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import titsch.guilherme.heartratemonitor.core.notification.NotificationManager

var coreModule = module {
    singleOf(::NotificationManager)
    single {
        get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    }
}