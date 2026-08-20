package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import androidx.annotation.CheckResult
object NetworkUtil {
    const val STATE_CODE_UNKNOWN = 0
    const val STATE_CODE_WIFI = 1
    const val STATE_CODE_MOBILE = 2
    const val STATE_CODE_OFFLINE = 3
}

@CheckResult
fun Context.networkState(): Int {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (!OSVersion.android6()) {
        val activeNetwork = connectivityManager.activeNetworkInfo
            ?: return NetworkUtil.STATE_CODE_OFFLINE
        return when (activeNetwork.type) {
                TYPE_WIFI -> NetworkUtil.STATE_CODE_WIFI
                TYPE_MOBILE -> NetworkUtil.STATE_CODE_MOBILE
            else -> NetworkUtil.STATE_CODE_UNKNOWN
        }
    }

    val activeNetwork = connectivityManager.activeNetwork
        ?: return NetworkUtil.STATE_CODE_OFFLINE
    val capabilities =
        connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return NetworkUtil.STATE_CODE_UNKNOWN

    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkUtil.STATE_CODE_WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkUtil.STATE_CODE_MOBILE
        else -> NetworkUtil.STATE_CODE_UNKNOWN
    }
}
