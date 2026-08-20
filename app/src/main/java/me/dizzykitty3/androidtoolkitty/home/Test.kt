package me.dizzykitty3.androidtoolkitty.home

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import me.dizzykitty3.androidtoolkitty.uicomponents.BaseCard
import me.dizzykitty3.androidtoolkitty.uicomponents.SpacerPadding
import me.dizzykitty3.androidtoolkitty.utils.ContactUtil
import me.dizzykitty3.androidtoolkitty.utils.NotificationUtil

@Composable
fun Test() {
    BaseCard(title = "test") {
        val view = LocalView.current
        val activity = LocalActivity.current
        Button(
            onClick = { NotificationUtil.createNotificationChannel(view.context) }
        ) {
            Text("create notification channel")
        }
      Button(onClick = { NotificationUtil.sendNotification(view.context) }) { Text("post notification") }

        SpacerPadding()
        HorizontalDivider()
        SpacerPadding()

        Button(
            onClick = { ContactUtil.createContact(activity) }
        ) {
            Text("create contact")
        }
    }
}
