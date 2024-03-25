package me.dizzykitty3.androidtoolkitty.view.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomAndroidApiLevelAndName
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomCard
import me.dizzykitty3.androidtoolkitty.common.ui.component.CustomSingleLineText

@Composable
fun AndroidVersionsCard() {
    val c = LocalContext.current
    CustomCard(
        icon = Icons.Outlined.Android,
        title = c.getString(R.string.android_versions)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(0.4f)
            ) {
                CustomSingleLineText(text = "Android 15")
                CustomSingleLineText(text = "Android 14")
                CustomSingleLineText(text = "Android 13")
                CustomSingleLineText(text = "Android 12L")
                CustomSingleLineText(text = "Android 12")
                CustomSingleLineText(text = "Android 11")
                CustomSingleLineText(text = "Android 10")
                CustomSingleLineText(text = "Android 9")
                CustomSingleLineText(text = "Android 8.1")
                CustomSingleLineText(text = "Android 8")
                CustomSingleLineText(text = "Android 7.1.1")
                CustomSingleLineText(text = "Android 7")
                CustomSingleLineText(text = "Android 6")
                CustomSingleLineText(text = "Android 5.1")
                CustomSingleLineText(text = "Android 5")
            }
            Column(
                modifier = Modifier.weight(0.6f)
            ) {
                CustomAndroidApiLevelAndName(text = "VanillaIceCream")
                CustomAndroidApiLevelAndName(text = "API 34, UpsideDownCake")
                CustomAndroidApiLevelAndName(text = "API 33, Tiramisu")
                CustomAndroidApiLevelAndName(text = "API 32, Sv2")
                CustomAndroidApiLevelAndName(text = "API 31, S")
                CustomAndroidApiLevelAndName(text = "API 30, R")
                CustomAndroidApiLevelAndName(text = "API 29, Q")
                CustomAndroidApiLevelAndName(text = "API 28, Pie")
                CustomAndroidApiLevelAndName(text = "API 27, Oreo")
                CustomAndroidApiLevelAndName(text = "API 26, Oreo")
                CustomAndroidApiLevelAndName(text = "API 25, Nougat")
                CustomAndroidApiLevelAndName(text = "API 24, Nougat")
                CustomAndroidApiLevelAndName(text = "API 23, Marshmallow")
                CustomAndroidApiLevelAndName(text = "API 22, Lollipop")
                CustomAndroidApiLevelAndName(text = "API 21, Lollipop")
            }
        }
    }
}