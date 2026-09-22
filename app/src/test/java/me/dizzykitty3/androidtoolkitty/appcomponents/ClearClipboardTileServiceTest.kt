package me.dizzykitty3.androidtoolkitty.appcomponents

import android.service.quicksettings.Tile
import me.dizzykitty3.androidtoolkitty.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ClearClipboardTileServiceTest {

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
