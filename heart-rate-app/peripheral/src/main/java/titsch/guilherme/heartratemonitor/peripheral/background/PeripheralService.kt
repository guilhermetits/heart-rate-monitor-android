package titsch.guilherme.heartratemonitor.peripheral.background

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.koin.android.ext.android.inject
import timber.log.Timber
import titsch.guilherme.heartratemonitor.core.notification.NotificationManager
import titsch.guilherme.heartratemonitor.peripheral.PeripheralApp
import titsch.guilherme.heartratemonitor.peripheral.R
import titsch.guilherme.heartratemonitor.peripheral.ui.MainActivity

class PeripheralService : Service() {

    private val peripheralController by inject<PeripheralController>()
    private val notificationManager by inject<NotificationManager>()

    override fun onCreate() {
        super.onCreate()
        peripheralController.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        peripheralController.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            handleIntent(it)
        }
        return START_STICKY
    }

    private fun handleIntent(it: Intent) = when {
        it.getBooleanExtra(STOP_FOREGROUND_SERVICE_ACTION, false) -> {
            Timber.d("Stopping Foreground Service")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        else -> {
            Timber.d("Starting Foreground Service")
            startForeground(CENTRAL_SERVICE_NOTIFICATION_ID, configureNotification())
        }
    }

    private fun configureNotification() =
        notificationManager.createForegroundServiceNotification(
            NotificationManager.ForegroundNotificationData(
                channelId = PeripheralApp.NOTIFICATION_CHANNEL_ID,
                icon = R.mipmap.ic_launcher,
                title = getString(R.string.peripheral_foreground_notification_title),
                content = getString(R.string.peripheral_foreground_notification_content),
                contentAction = createMainActivityIntent(this),
                actionText = getString(R.string.peripheral_foreground_notification_action_stop),
                action = createStopForegroundServiceIntent(this)
            )
        )

    override fun onBind(intent: Intent?): IBinder {
        return CentralServiceBinder()
    }

    inner class CentralServiceBinder : Binder()

    companion object {
        const val CENTRAL_SERVICE_NOTIFICATION_ID = 10001
        const val STOP_FOREGROUND_SERVICE_ACTION = "STOP_FOREGROUND_SERVICE_ACTION"
        fun createIntent(context: Context): Intent = Intent(context, PeripheralService::class.java)

        private fun createMainActivityIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        private fun createStopForegroundServiceIntent(context: Context): PendingIntent {
            val intent = Intent(context, PeripheralService::class.java)
                .putExtra(STOP_FOREGROUND_SERVICE_ACTION, true)
            return PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
