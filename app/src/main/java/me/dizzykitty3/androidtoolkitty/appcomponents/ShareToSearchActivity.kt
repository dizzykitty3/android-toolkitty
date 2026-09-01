package me.dizzykitty3.androidtoolkitty.appcomponents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openSearch
import timber.log.Timber

class ShareToSearchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        val intent = intent
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text != null) {
            Timber.i("onCreate text non null")
            this.openSearch(text)
        }
        finish()
    }
}