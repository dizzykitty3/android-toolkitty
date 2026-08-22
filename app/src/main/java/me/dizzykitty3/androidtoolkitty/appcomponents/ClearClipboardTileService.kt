package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.OSVersion
import timber.log.Timber

class ClearClipboardTileService : TileService() {
    override fun onBind(intent: Intent?): IBinder? {
        if (!OSVersion.android7()) {
            Timber.w("TileService unsupported")
            return null
        }

        Timber.d("onBind")
        return super.onBind(intent)
    }

    override fun onStartListening() {
        if (!OSVersion.android7()) {
            Timber.w("TileService unsupported")
            return
        }

        super.onStartListening()
        Timber.d("onStartListening")
        val cleanTitle = qsTile
        cleanTitle.label = getString(R.string.clear_clipboard)
        cleanTitle.state = Tile.STATE_INACTIVE
        cleanTitle.icon = Icon.createWithResource(this, R.drawable.ic_launcher_foreground)
        cleanTitle.updateTile()
    }

    override fun onClick() {
        if (!OSVersion.android7()) {
            Timber.w("TileService unsupported")
            return
        }

        super.onClick()
        Timber.d("onClick")
        try {
            val intent = Intent(this@ClearClipboardTileService, ClearClipboardActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            if (OSVersion.android14()) {
                Timber.i("Android 14")
                val pendingIntent = PendingIntent.getActivity(
                    this@ClearClipboardTileService,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                Timber.i("< Android 14")
                startActivityAndCollapse(intent)
            }
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }
}