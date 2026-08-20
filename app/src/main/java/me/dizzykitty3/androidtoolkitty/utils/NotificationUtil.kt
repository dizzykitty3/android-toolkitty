package me.dizzykitty3.androidtoolkitty.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Activity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtil.noNotificationPermission
import me.dizzykitty3.androidtoolkitty.utils.PermissionUtil.requestNotificationPermission

object NotificationUtil {
    private const val CHANNEL_ID = "test_channel"
    private var count = 1

    fun createNotificationChannel(activity: Activity) {
        val context = activity.applicationContext
        if (OSVersion.android8()) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (context.noNotificationPermission()) {
            activity.requestNotificationPermission()
        }
    }

    fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("text_title_$count")
            .setContentText("text_content_$count").setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(count, builder.build())
            count += 1
        }
    }
}
