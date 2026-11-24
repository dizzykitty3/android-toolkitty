package me.dizzykitty3.androidtoolkitty.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.material3.TextButton
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openAppDetailSettings
import me.dizzykitty3.androidtoolkitty.uicomponents.RowDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.ui.home.AppsActivity
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.utils.IntentUtils.openScreen

private const val MANUAL_PERMISSION_HINT_THRESHOLD = 2

private fun Context.canQueryAllPackages(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun Apps() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var granted by remember(context) { mutableStateOf(context.canQueryAllPackages()) }
    var requestCount by rememberSaveable { mutableIntStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted = context.canQueryAllPackages()
    }
    DisposableEffect(context, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) granted = context.canQueryAllPackages()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BaseCard(title = R.string.apps_title, icon = Icons.Outlined.Apps) {
        if (granted) {
            FilledTonalButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                granted = context.canQueryAllPackages()
                if (granted) context.openScreen(AppsActivity::class.java)
            }) { Text(stringResource(R.string.apps_installed_apps)) }
        } else {
            Text(stringResource(R.string.apps_permission_explanation))
            FilledTonalButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                requestCount++
                // Requesting access never navigates to the installed apps screen.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    permissionLauncher.launch(Manifest.permission.QUERY_ALL_PACKAGES)
                }
            }) { Text(stringResource(R.string.request_permission)) }
            if (requestCount >= MANUAL_PERMISSION_HINT_THRESHOLD) {
                RowDivider()
                Text(stringResource(R.string.apps_permission_settings_hint))
                TextButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    context.openAppDetailSettings()
                }) { Text(stringResource(R.string.go_to_settings)) }
            }
        }
    }
}
