package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.PendingIntent
import android.service.quicksettings.TileService
import android.service.quicksettings.Tile
import android.content.ComponentName
import android.content.Intent
import me.dizzykitty3.androidtoolkitty.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowService

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ClearClipboardTileServiceTest {

    @Test
    @Config(sdk = [34], shadows = [PendingIntentTileServiceShadow::class])
    fun click_onAndroid14_usesImmutablePendingIntentToLaunchClearClipboard() {
        val controller = Robolectric.buildService(ClearClipboardTileService::class.java).create()
        try {
            val service = controller.get()
            service.onClick()

            val tileShadow = Shadow.extract<PendingIntentTileServiceShadow>(service)
            val pendingIntent = requireNotNull(tileShadow.startedPendingIntent)
            assertTrue(pendingIntent.isActivity)
            assertTrue(pendingIntent.isImmutable)

            val application = shadowOf(RuntimeEnvironment.getApplication())
            assertNull(application.nextStartedActivity)
            pendingIntent.send()
            val intent = requireNotNull(application.nextStartedActivity)
            assertEquals(ComponentName(service, ClearClipboardActivity::class.java), intent.component)
            assertEquals(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
                intent.flags,
            )
            assertNull(application.nextStartedActivity)
        } finally {
            controller.destroy()
        }
    }

    @Implements(TileService::class)
    class PendingIntentTileServiceShadow : ShadowService() {
        var startedPendingIntent: PendingIntent? = null
            private set

        @Implementation(minSdk = 34)
        protected fun startActivityAndCollapse(pendingIntent: PendingIntent) {
            startedPendingIntent = pendingIntent
        }
    }

    @Test
    @Config(sdk = [33])
    fun click_onAndroid13_launchesClearClipboardOutsideRecents() {
        val service = Robolectric.buildService(ClearClipboardTileService::class.java).create().get()
        service.onClick()

        val application = shadowOf(RuntimeEnvironment.getApplication())
        val intent = requireNotNull(application.nextStartedActivity)
        assertEquals(ComponentName(service, ClearClipboardActivity::class.java), intent.component)
        assertEquals(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
            intent.flags,
        )
        assertNull(application.nextStartedActivity)
    }

    @Test
    fun startListening_initializesTheClearClipboardTile() {
        val service = Robolectric.buildService(ClearClipboardTileService::class.java).create().get()

        service.onStartListening()

        val tile = service.qsTile
        assertEquals(service.getString(R.string.clear_clipboard), tile.label)
        assertEquals(Tile.STATE_INACTIVE, tile.state)
        assertNotNull(tile.icon)
    }
}
