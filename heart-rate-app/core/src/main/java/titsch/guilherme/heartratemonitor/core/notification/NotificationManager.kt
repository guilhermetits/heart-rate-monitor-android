package titsch.guilherme.heartratemonitor.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificationManager(
    private val notificationManager: NotificationManager,
    private val context: Context,
) {
    fun createNotificationChannel(channelId: String, channelTitle: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelTitle,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun createForegroundServiceNotification(foregroundNotificationData: ForegroundNotificationData) =
        NotificationCompat.Builder(context, foregroundNotificationData.channelId).apply {
            setSmallIcon(foregroundNotificationData.icon)
            setContentTitle(foregroundNotificationData.title)
            setContentText(foregroundNotificationData.content)
            setOngoing(true)
            if (foregroundNotificationData.contentAction != null) {
                setContentIntent(foregroundNotificationData.contentAction)
            }
            if (foregroundNotificationData.action != null && foregroundNotificationData.actionText != null) {
                addAction(
                    0,
                    foregroundNotificationData.actionText,
                    foregroundNotificationData.action
                )
            }
        }.build()

    data class ForegroundNotificationData(
        val channelId: String,
        val icon: Int,
        val title: String,
        val content: String,
        val contentAction: PendingIntent? = null,
        val actionText: String? = null,
        val action: PendingIntent? = null
    )
}