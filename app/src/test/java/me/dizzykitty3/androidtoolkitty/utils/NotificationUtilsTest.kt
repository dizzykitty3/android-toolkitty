package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import me.dizzykitty3.androidtoolkitty.POST_NOTIFICATIONS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NotificationUtilsTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    fun createNotificationChannel_registersTheExpectedChannel() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(POST_NOTIFICATIONS)
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

        NotificationUtils.createNotificationChannel(activity)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel("test_channel")
        assertNotNull(channel)
        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_channel_name),
            channel?.name,
        )
    }

    @Test
    fun sendNotification_doesNotPostWhenNotificationPermissionIsDenied() {
        shadowOf(RuntimeEnvironment.getApplication()).denyPermissions(POST_NOTIFICATIONS)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationUtils.sendNotification(context)

        assertNull(shadowOf(manager).getNotification(1))
    }

    @Test
    fun sendNotification_postsTheExpectedNotificationWhenPermissionIsGranted() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(POST_NOTIFICATIONS)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationUtils.sendNotification(context)

        val notification = shadowOf(manager).getNotification(1)
        assertNotNull(notification)
        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_title, 1),
            notification?.extras?.getCharSequence(Notification.EXTRA_TITLE),
        )
    }
}
