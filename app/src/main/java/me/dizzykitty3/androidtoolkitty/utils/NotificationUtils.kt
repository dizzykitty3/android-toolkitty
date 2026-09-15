package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.noNotificationPermission
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtils.requestNotificationPermission

object NotificationUtils {
    private const val CHANNEL_ID = "test_channel"
    private var notificationId = 1

    fun createNotificationChannel(activity: Activity) {
        val context = activity.applicationContext
        if (OSVersion.android8()) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService<NotificationManager>()
                ?: return
            notificationManager.createNotificationChannel(channel)
        }

        if (context.noNotificationPermission()) {
            activity.requestNotificationPermission()
        }
    }

    fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("text_title_$notificationId")
            .setContentText("text_content_$notificationId")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (context.noNotificationPermission()) {
                return@with
            }
            notify(notificationId, builder.build())
            notificationId += 1
        }
    }
}
