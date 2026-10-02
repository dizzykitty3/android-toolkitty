package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.utils.ToastUtils.showToast
import me.dizzykitty3.androidtoolkitty.utils.copyToClipboard
import timber.log.Timber

class ShareToClipboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        val shareIntent = intent
        val sharedText = shareIntent.getStringExtra(Intent.EXTRA_TEXT)

        if (sharedText != null) {
            Timber.i("onCreate sharedText non null")
            copyToClipboard(sharedText)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                showToast(R.string.copied)
            }
        }
        finish()
    }
}
