package me.dizzykitty3.androidtoolkitty.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService

@CheckResult
fun Context.clearClipboard(): Boolean {
    val clipboard = getSystemService<ClipboardManager>() ?: return false
    if (!clipboard.hasPrimaryClip()) return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        clipboard.clearPrimaryClip()
        return true
    }
    copyToClipboard("")
    return true
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService<ClipboardManager>() ?: return
    val clip = ClipData.newPlainText("", text)
    clipboard.setPrimaryClip(clip)
}
