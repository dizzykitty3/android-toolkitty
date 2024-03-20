package me.dizzykitty3.androidtoolkitty.view.card

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomCard
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomSpacerPadding
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomSystemSettingsButton
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomTip

@Composable
fun SystemSettingsCard() {
    val c = LocalContext.current
    CustomCard(
        icon = Icons.Outlined.Settings,
        title = c.getString(R.string.android_system_settings),
        id = "card_android_system_settings"
    ) {
        if (!checkIsAutoTime(c)) {
            CustomTip(
                text = "\"Set time automatically\" is now OFF, you may meet some unexpected behavior while using your phone."
            )
        }
        Text(
            text = c.getString(R.string.common),
            style = MaterialTheme.typography.titleMedium
        )
        CustomSpacerPadding()
        CustomSystemSettingsButton(
            settingType = "display",
            buttonText = c.getString(R.string.open_display_settings)
        )
        CustomSystemSettingsButton(
            settingType = "auto_rotate",
            buttonText = c.getString(R.string.open_auto_rotate_settings)
        )
        CustomSystemSettingsButton(
            settingType = "manage_default_apps",
            buttonText = c.getString(R.string.open_default_apps_settings)
        )
        CustomSystemSettingsButton(
            settingType = "ignore_battery_optimization",
            buttonText = c.getString(R.string.open_battery_optimization_settings)
        )
        CustomSystemSettingsButton(
            settingType = "captioning",
            buttonText = c.getString(R.string.open_caption_preferences)
        )
        CustomSpacerPadding()
        HorizontalDivider()
        CustomSpacerPadding()
        Text(
            text = c.getString(R.string.debugging),
            style = MaterialTheme.typography.titleMedium
        )
        CustomSpacerPadding()
        CustomSystemSettingsButton(
            settingType = "locale",
            buttonText = c.getString(R.string.open_language_settings)
        )
        CustomSystemSettingsButton(
            settingType = "date",
            buttonText = c.getString(R.string.open_date_and_time_settings)
        )
        CustomSystemSettingsButton(
            settingType = "development_settings",
            buttonText = c.getString(R.string.open_developer_options)
        )
    }
}

private fun checkIsAutoTime(c: Context): Boolean {
    val contentResolver: ContentResolver = c.contentResolver
    val isAutoTime = Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME, 0)
    return isAutoTime == 1
}