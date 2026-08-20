package me.dizzykitty3.androidtoolkitty.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.CheckResult

@CheckResult
fun Context.clearClipboard(): Boolean {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (!clipboard.hasPrimaryClip()) return false
    if (OSVersion.android9()) {
        clipboard.clearPrimaryClip()
        return true
    }
    copyToClipboard("")
    return true
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("", text)
    clipboard.setPrimaryClip(clip)
}
