package me.dizzykitty3.androidtoolkitty.ui.edit

import android.view.HapticFeedbackConstants
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.dizzykitty3.androidtoolkitty.CARD_1
import me.dizzykitty3.androidtoolkitty.CARD_10
import me.dizzykitty3.androidtoolkitty.CARD_11
import me.dizzykitty3.androidtoolkitty.CARD_12
import me.dizzykitty3.androidtoolkitty.CARD_2
import me.dizzykitty3.androidtoolkitty.CARD_3
import me.dizzykitty3.androidtoolkitty.CARD_4
import me.dizzykitty3.androidtoolkitty.CARD_5
import me.dizzykitty3.androidtoolkitty.CARD_6
import me.dizzykitty3.androidtoolkitty.CARD_7
import me.dizzykitty3.androidtoolkitty.CARD_8
import me.dizzykitty3.androidtoolkitty.CARD_9
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.data.sharedpreferences.SettingsSharedPref
import me.dizzykitty3.androidtoolkitty.ui_components.Card
import me.dizzykitty3.androidtoolkitty.ui_components.CustomHideCardSettingSwitch
import me.dizzykitty3.androidtoolkitty.ui_components.GroupDivider
import me.dizzykitty3.androidtoolkitty.ui_components.Screen
import me.dizzykitty3.androidtoolkitty.ui_components.SpacerPadding

@Composable
fun HomeEdit() {
    Screen {
        val settingsSharedPref = remember { SettingsSharedPref }
        val isShowCard5 = settingsSharedPref.getCardShowedState(CARD_5)
        var mIsShowCard5 by remember { mutableStateOf(isShowCard5) }

        Card(
            title = R.string.customize_my_home_page
        ) {
            val view = LocalView.current
            var mIsShowCard1 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_1
                    )
                )
            }
            var mIsShowCard2 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_2
                    )
                )
            }
            var mIsShowCard3 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_3
                    )
                )
            }
            var mIsShowCard4 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_4
                    )
                )
            }
            var mIsShowCard6 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_5
                    )
                )
            }
            var mIsShowCard7 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_6
                    )
                )
            }
            var mIsShowCard8 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_7
                    )
                )
            }
            var mIsShowCard9 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_8
                    )
                )
            }
            var mIsShowCard10 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_9
                    )
                )
            }
            var mIsShowCard11 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_10
                    )
                )
            }
            var mIsShowCard12 by remember {
                mutableStateOf(
                    settingsSharedPref.getCardShowedState(
                        CARD_11
                    )
                )
            }

            CustomHideCardSettingSwitch(
                text = R.string.year_progress,
                card = CARD_1,
                isChecked = mIsShowCard1
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard1 = newState
                settingsSharedPref.saveCardShowedState(CARD_1, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.volume,
                card = CARD_2,
                isChecked = mIsShowCard2
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard2 = newState
                settingsSharedPref.saveCardShowedState(CARD_2, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.clipboard,
                card = CARD_3,
                isChecked = mIsShowCard3
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard3 = newState
                settingsSharedPref.saveCardShowedState(CARD_3, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.url,
                card = CARD_4,
                isChecked = mIsShowCard4
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard4 = newState
                settingsSharedPref.saveCardShowedState(CARD_4, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.system_settings,
                card = CARD_5,
                isChecked = mIsShowCard5
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard5 = newState
                settingsSharedPref.saveCardShowedState(CARD_5, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.wheel_of_fortune,
                card = CARD_6,
                isChecked = mIsShowCard6
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard6 = newState
                settingsSharedPref.saveCardShowedState(CARD_6, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.bluetooth_devices,
                card = CARD_7,
                isChecked = mIsShowCard7
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard7 = newState
                settingsSharedPref.saveCardShowedState(CARD_7, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.unicode,
                card = CARD_8,
                isChecked = mIsShowCard8
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard8 = newState
                settingsSharedPref.saveCardShowedState(CARD_8, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.check_app_on_market,
                card = CARD_9,
                isChecked = mIsShowCard9
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard9 = newState
                settingsSharedPref.saveCardShowedState(CARD_9, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.google_maps,
                card = CARD_10,
                isChecked = mIsShowCard10
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard10 = newState
                settingsSharedPref.saveCardShowedState(CARD_10, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.android_versions,
                card = CARD_11,
                isChecked = mIsShowCard11
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard11 = newState
                settingsSharedPref.saveCardShowedState(CARD_11, newState)
            }
            CustomHideCardSettingSwitch(
                text = R.string.font_weight_test,
                card = CARD_12,
                isChecked = mIsShowCard12
            ) { newState ->
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mIsShowCard12 = newState
                settingsSharedPref.saveCardShowedState(CARD_12, newState)
            }

            GroupDivider()

            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onClickChangeAllCardsButton(false)
                        mIsShowCard1 = false
                        mIsShowCard2 = false
                        mIsShowCard3 = false
                        mIsShowCard4 = false
                        mIsShowCard5 = false
                        mIsShowCard6 = false
                        mIsShowCard7 = false
                        mIsShowCard8 = false
                        mIsShowCard9 = false
                        mIsShowCard10 = false
                        mIsShowCard11 = false
                        mIsShowCard12 = false
                },
                elevation = ButtonDefaults.buttonElevation(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = stringResource(id = R.string.hide_all_cards),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SpacerPadding()
                Text(text = stringResource(R.string.hide_all_cards))
            }

            Button(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onClickChangeAllCardsButton(true)
                        mIsShowCard1 = true
                        mIsShowCard2 = true
                        mIsShowCard3 = true
                        mIsShowCard4 = true
                        mIsShowCard5 = true
                        mIsShowCard6 = true
                        mIsShowCard7 = true
                        mIsShowCard8 = true
                        mIsShowCard9 = true
                        mIsShowCard10 = true
                        mIsShowCard11 = true
                        mIsShowCard12 = true
                },
                elevation = ButtonDefaults.buttonElevation(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = stringResource(id = R.string.show_all_cards),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                SpacerPadding()
                Text(text = stringResource(R.string.show_all_cards))
            }
        }

        if (mIsShowCard5) SysSettingsCardEdit()
    }
}

private fun onClickChangeAllCardsButton(isShow: Boolean) {
    val cardList = listOf(
        CARD_1,
        CARD_2,
        CARD_3,
        CARD_4,
        CARD_5,
        CARD_6,
        CARD_7,
        CARD_8,
        CARD_9,
        CARD_10,
        CARD_11,
        CARD_12
    )
    val settingsViewModel = SettingsSharedPref

    for (card in cardList) {
        settingsViewModel.saveCardShowedState(card, isShow)
    }
}