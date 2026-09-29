package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.ToastUtils.showToast
import timber.log.Timber

class ClearClipboardTileService : TileService() {
    override fun onBind(intent: Intent?): IBinder? {
        Timber.d("onBind")
        return super.onBind(intent)
    }

    override fun onStartListening() {
        super.onStartListening()
        Timber.d("onStartListening")
        val tile = qsTile
        tile.label = getString(R.string.clear_clipboard)
        tile.state = Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, android.R.drawable.ic_delete)
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        Timber.d("onClick")
        try {
            val intent = Intent(this@ClearClipboardTileService, ClearClipboardActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
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
            showToast(R.string.error)
        }
    }
}