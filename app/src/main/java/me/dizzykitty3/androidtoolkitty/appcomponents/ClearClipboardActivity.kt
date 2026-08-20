package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.clearClipboard
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
            if (clearClipboard()) {
                Timber.i("clipboard cleared")
                toast(getString(R.string.clipboard_cleared))
            } else {
                Timber.i("clipboard is empty")
                toast(getString(R.string.clipboard_is_empty))
            }
            finish()
        }
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
