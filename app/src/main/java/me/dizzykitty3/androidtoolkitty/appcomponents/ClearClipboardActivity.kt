package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.Activity
import android.os.Bundle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.clearClipboard
import me.dizzykitty3.androidtoolkitty.utils.ToastUtils.showToast
import timber.log.Timber

class ClearClipboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Timber.d("onWindowFocusChanged")
        if (hasFocus) {
            Timber.d("hasFocus")
            val message = if (clearClipboard()) {
                Timber.i("clipboard cleared")
                R.string.clipboard_cleared
            } else {
                Timber.i("clipboard is empty")
                R.string.clipboard_is_empty
            }
            showToast(message)
            finish()
        }
    }

}
