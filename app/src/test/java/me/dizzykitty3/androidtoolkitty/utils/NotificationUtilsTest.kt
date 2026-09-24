package me.dizzykitty3.androidtoolkitty.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import me.dizzykitty3.androidtoolkitty.POST_NOTIFICATIONS
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
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

    @Before
    fun resetNotificationId() {
        NotificationUtils::class.java.getDeclaredField("notificationId").apply {
            isAccessible = true
            setInt(null, 1)
        }
    }

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
        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_channel_description),
            channel?.description,
        )
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, channel?.importance)
    }

    @Test
    fun createNotificationChannel_requestsPermissionWhenItIsDenied() {
        shadowOf(RuntimeEnvironment.getApplication()).denyPermissions(POST_NOTIFICATIONS)
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()

        NotificationUtils.createNotificationChannel(activity)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assertNotNull(manager.getNotificationChannel("test_channel"))
        assertArrayEquals(
            arrayOf(POST_NOTIFICATIONS),
            requireNotNull(shadowOf(activity).lastRequestedPermission).requestedPermissions,
        )
    }

    @Test
    fun createNotificationChannel_repeatedInitializationPreservesExistingImportance() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(POST_NOTIFICATIONS)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel("test_channel", "Existing channel", NotificationManager.IMPORTANCE_LOW),
        )
        val controller = Robolectric.buildActivity(Activity::class.java)
        try {
            val activity = controller.setup().get()

            repeat(2) { NotificationUtils.createNotificationChannel(activity) }

            assertEquals(1, manager.notificationChannels.size)
            assertEquals(
                NotificationManager.IMPORTANCE_LOW,
                manager.getNotificationChannel("test_channel").importance,
            )
            assertNull(shadowOf(activity).lastRequestedPermission)
        } finally {
            controller.pause().stop().destroy()
        }
    }

    @Test
    fun sendNotification_usesRegisteredChannelAndValidSmallIcon() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(POST_NOTIFICATIONS)
        val controller = Robolectric.buildActivity(Activity::class.java)
        try {
            val activity = controller.setup().get()
            NotificationUtils.createNotificationChannel(activity)

            NotificationUtils.sendNotification(context)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = requireNotNull(shadowOf(manager).getNotification(1))
            assertEquals("test_channel", notification.channelId)
            assertNotNull(manager.getNotificationChannel(notification.channelId))
            assertNotNull(notification.smallIcon)
            assertEquals(android.R.drawable.ic_dialog_info, notification.smallIcon.resId)
        } finally {
            controller.pause().stop().destroy()
        }
    }

    @Test
    fun sendNotification_doesNotPostWhenNotificationPermissionIsDenied() {
        shadowOf(RuntimeEnvironment.getApplication()).denyPermissions(POST_NOTIFICATIONS)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationUtils.sendNotification(context)

        assertNull(shadowOf(manager).getNotification(1))
    }

    @Test
    fun sendNotification_resumesWithoutSkippingIdsAfterPermissionChanges() {
        val application = shadowOf(RuntimeEnvironment.getApplication())
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = shadowOf(manager)

        application.denyPermissions(POST_NOTIFICATIONS)
        NotificationUtils.sendNotification(context)
        assertNull(notifications.getNotification(1))

        application.grantPermissions(POST_NOTIFICATIONS)
        NotificationUtils.sendNotification(context)
        val first = requireNotNull(notifications.getNotification(1))

        application.denyPermissions(POST_NOTIFICATIONS)
        NotificationUtils.sendNotification(context)
        assertNull(notifications.getNotification(2))

        application.grantPermissions(POST_NOTIFICATIONS)
        NotificationUtils.sendNotification(context)
        val second = requireNotNull(notifications.getNotification(2))

        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_title, 1),
            first.extras.getCharSequence(Notification.EXTRA_TITLE),
        )
        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_title, 2),
            second.extras.getCharSequence(Notification.EXTRA_TITLE),
        )
        assertEquals(2, manager.activeNotifications.size)
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
        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_content, 1),
            notification?.extras?.getCharSequence(Notification.EXTRA_TEXT),
        )
    }

    @Test
    fun sendNotification_incrementsTheIdAndDisplayedNumberForEachPost() {
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(POST_NOTIFICATIONS)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationUtils.sendNotification(context)
        NotificationUtils.sendNotification(context)

        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_title, 1),
            shadowOf(manager).getNotification(1)?.extras?.getCharSequence(Notification.EXTRA_TITLE),
        )
        assertEquals(
            context.getString(me.dizzykitty3.androidtoolkitty.R.string.notification_title, 2),
            shadowOf(manager).getNotification(2)?.extras?.getCharSequence(Notification.EXTRA_TITLE),
        )
    }
}
