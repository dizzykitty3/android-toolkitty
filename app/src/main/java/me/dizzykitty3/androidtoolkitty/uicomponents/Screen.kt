package me.dizzykitty3.androidtoolkitty.uicomponents

import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import me.dizzykitty3.androidtoolkitty.R

@Composable
fun Screen(
    @StringRes screenTitle: Int, navController: NavHostController, content: @Composable () -> Unit
) = Screen(stringResource(screenTitle), navController, content)

@Composable
fun Screen(
    screenTitle: String = "", navController: NavHostController, content: @Composable () -> Unit
) {
    val screenPadding = dimensionResource(R.dimen.padding_screen)

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            Modifier.padding(start = screenPadding, end = screenPadding)
        ) {
            TopPadding()
            TopBar(screenTitle, navController)
            LazyColumn {
                item { content() }
                item { BottomPadding() }
            }
        }
    }
}

@Composable
fun Screen(
    @StringRes screenTitle: Int, content: @Composable () -> Unit
) = Screen(stringResource(screenTitle), content)

@Composable
fun Screen(
    screenTitle: String = "", content: @Composable () -> Unit
) {
    val screenPadding = dimensionResource(R.dimen.padding_screen)

    Column(
        Modifier.padding(start = screenPadding, end = screenPadding)
    ) {
        TopPadding()
        TopBar(screenTitle)
        LazyColumn {
            item { content() }
            item { BottomPadding() }
        }
    }
}

@Composable
fun LicenseScreen(
    @StringRes screenTitle: Int, content: @Composable () -> Unit
) {
    val screenPadding = dimensionResource(R.dimen.padding_screen)

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            Modifier.padding(start = screenPadding, end = screenPadding)
        ) {
            TopPadding()
            TopBar(screenTitle)
            Column {
                content()
                BottomPadding()
            }
        }
    }
}

@Composable
fun TopBar(@StringRes screenTitle: Int, navController: NavHostController) =
    TopBar(stringResource(screenTitle), navController)

@Composable
fun TopBar(screenTitle: String = "", navController: NavHostController) {
    val haptic = LocalHapticFeedback.current

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            navController.popBackStack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5F)
            )
        }
        IconAndTextPadding()
        Column(
            Modifier.weight(1F), verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = screenTitle, style = MaterialTheme.typography.titleLarge
            )
        }
    }
    SpacerPadding()
}

@Composable
fun TopBar(@StringRes screenTitle: Int) = TopBar(stringResource(screenTitle))

@Composable
fun TopBar(screenTitle: String = "") {
    val haptic = LocalHapticFeedback.current
    val activity = LocalActivity.current

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            activity?.finish()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5F)
            )
        }
        IconAndTextPadding()
        Column(
            Modifier.weight(1F), verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = screenTitle, style = MaterialTheme.typography.titleLarge
            )
        }
    }
    SpacerPadding()
}
