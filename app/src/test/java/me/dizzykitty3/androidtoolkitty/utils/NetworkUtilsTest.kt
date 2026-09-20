package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NetworkUtilsTest {

    @Test
    fun networkState_reportsOfflineWhenThereIsNoActiveNetwork() {
        val context: Context = RuntimeEnvironment.getApplication()
        val connectivityManager = requireNotNull(context.getSystemService<ConnectivityManager>())
        shadowOf(connectivityManager).clearAllNetworks()

        assertEquals(NetworkUtil.STATE_CODE_OFFLINE, context.networkState())
    }
}
