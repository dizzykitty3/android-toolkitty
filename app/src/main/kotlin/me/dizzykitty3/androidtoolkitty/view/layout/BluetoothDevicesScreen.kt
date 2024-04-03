package me.dizzykitty3.androidtoolkitty.view.layout

import androidx.compose.runtime.Composable
import me.dizzykitty3.androidtoolkitty.foundation.ui_component.CustomScreen
import me.dizzykitty3.androidtoolkitty.view.card.BluetoothDevicesCard

@Composable
fun BluetoothDevicesScreen() {
    CustomScreen {
        BluetoothDevicesCard()
    }
}