package titsch.guilherme.heartratemonitor.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager

class NotificationManager(private val notificationManager: NotificationManager) {
    fun createNotificationChannel(channelId: String, channelTitle: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelTitle,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}