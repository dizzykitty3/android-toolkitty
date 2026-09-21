package me.dizzykitty3.androidtoolkitty.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SystemSettingDefinitionsTest {

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
}
