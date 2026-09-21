package me.dizzykitty3.androidtoolkitty.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import androidx.core.content.getSystemService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities
import org.robolectric.shadows.ShadowNetworkInfo

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

    @Test
    fun networkState_reportsWifiForAnActiveWifiNetwork() {
        val context: Context = RuntimeEnvironment.getApplication()
        val connectivityManager = requireNotNull(context.getSystemService<ConnectivityManager>())
        val shadowConnectivityManager = shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(
            ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_WIFI,
                0,
                true,
                true,
            ),
        )
        shadowConnectivityManager.setNetworkCapabilities(
            requireNotNull(connectivityManager.activeNetwork),
            networkCapabilitiesFor(NetworkCapabilities.TRANSPORT_WIFI),
        )

        assertEquals(NetworkUtil.STATE_CODE_WIFI, context.networkState())
    }

    @Test
    fun networkState_reportsMobileForAnActiveCellularNetwork() {
        val context: Context = RuntimeEnvironment.getApplication()
        val connectivityManager = requireNotNull(context.getSystemService<ConnectivityManager>())
        val shadowConnectivityManager = shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(
            ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_MOBILE,
                0,
                true,
                true,
            ),
        )
        shadowConnectivityManager.setNetworkCapabilities(
            requireNotNull(connectivityManager.activeNetwork),
            networkCapabilitiesFor(NetworkCapabilities.TRANSPORT_CELLULAR),
        )

        assertEquals(NetworkUtil.STATE_CODE_MOBILE, context.networkState())
    }

    @Test
    fun networkState_reportsUnknownForAnUnsupportedTransport() {
        val context: Context = RuntimeEnvironment.getApplication()
        val connectivityManager = requireNotNull(context.getSystemService<ConnectivityManager>())
        val shadowConnectivityManager = shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(
            ShadowNetworkInfo.newInstance(
                NetworkInfo.DetailedState.CONNECTED,
                ConnectivityManager.TYPE_VPN,
                0,
                true,
                true,
            ),
        )
        shadowConnectivityManager.setNetworkCapabilities(
            requireNotNull(connectivityManager.activeNetwork),
            networkCapabilitiesFor(NetworkCapabilities.TRANSPORT_VPN),
        )

        assertEquals(NetworkUtil.STATE_CODE_UNKNOWN, context.networkState())
    }

    private fun networkCapabilitiesFor(transportType: Int): NetworkCapabilities =
        ShadowNetworkCapabilities.newInstance().also { capabilities ->
            shadowOf(capabilities).addTransportType(transportType)
        }
}
