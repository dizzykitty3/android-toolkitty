package me.dizzykitty3.androidtoolkitty.home

import android.content.Context
import me.dizzykitty3.androidtoolkitty.S_ALARMS
import me.dizzykitty3.androidtoolkitty.S_APP_NOTIFICATIONS
import me.dizzykitty3.androidtoolkitty.S_AUTO_ROTATE
import me.dizzykitty3.androidtoolkitty.S_MEDIA_MANAGEMENT
import me.dizzykitty3.androidtoolkitty.S_SEARCH_SETTINGS
import me.dizzykitty3.androidtoolkitty.S_UNKNOWN_APPS
import me.dizzykitty3.androidtoolkitty.utils.systemSettingsIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SystemSettingDefinitionsTest {

    private val context: Context
        get() = RuntimeEnvironment.getApplication()

    @Test
    @Config(sdk = [25])
    fun android71_hidesSettingsIntroducedAfterItsRelease() {
        assertUnavailableSettings(
            S_UNKNOWN_APPS, S_SEARCH_SETTINGS, S_AUTO_ROTATE,
            S_ALARMS, S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS,
        )
    }

    @Test
    @Config(sdk = [26, 28])
    fun android8Through9_exposesUnknownAppSettingsButNotNewerSettings() {
        assertUnavailableSettings(
            S_SEARCH_SETTINGS, S_AUTO_ROTATE, S_ALARMS,
            S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS,
        )
    }

    @Test
    @Config(sdk = [29, 30])
    fun android10And11_exposeSearchSettingsButNotNewerSettings() {
        assertUnavailableSettings(S_AUTO_ROTATE, S_ALARMS, S_MEDIA_MANAGEMENT, S_APP_NOTIFICATIONS)
    }

    @Test
    @Config(sdk = [31, 32])
    fun android12And12L_hideOnlyAllAppNotificationSettings() {
        assertUnavailableSettings(S_APP_NOTIFICATIONS)
    }

    @Test
    @Config(sdk = [33])
    fun android13_exposesEveryDefinedSetting() {
        assertUnavailableSettings()
    }

    private fun assertUnavailableSettings(vararg unavailableTypes: String) {
        val unavailable = unavailableTypes.toSet()
        val available = availableSystemSettings()
        assertEquals(
            unavailable,
            (systemSettingDefinitions - available.toSet()).map { it.settingType }.toSet(),
        )
        assertEquals(
            systemSettingDefinitions.filterNot { it.settingType in unavailable },
            available,
        )
        available.forEach { setting ->
            assertNotNull(setting.settingType, systemSettingsIntent(setting.settingType))
        }
    }

    @Test
    fun definitions_haveUniqueTypesAndAvailableSettingsAreAValidSubset() {
        val settingTypes = systemSettingDefinitions.map { it.settingType }
        val availableSettings = availableSystemSettings()

        assertEquals(settingTypes.size, settingTypes.distinct().size)
        assertTrue(availableSettings.isNotEmpty())
        assertTrue(availableSettings.all { it in systemSettingDefinitions })
        assertTrue(availableSettings.all { it.isAvailable() })
    }

    @Test
    fun currentAndroidVersion_exposesEveryDefinedSystemSetting() {
        assertEquals(systemSettingDefinitions, availableSystemSettings())
    }

    @Test
    fun definitions_referenceNonBlankTitleResources() {
        systemSettingDefinitions.forEach { setting ->
            assertTrue(context.getString(setting.title).isNotBlank())
        }
    }

    @Test
    fun availableSettings_allResolveToSystemIntents() {
        availableSystemSettings().forEach { setting ->
            assertNotNull(systemSettingsIntent(setting.settingType))
        }
    }
}
