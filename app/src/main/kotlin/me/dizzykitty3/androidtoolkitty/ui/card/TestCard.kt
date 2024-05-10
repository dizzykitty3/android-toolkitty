package me.dizzykitty3.androidtoolkitty.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.dizzykitty3.androidtoolkitty.foundation.composable.CustomCard
import me.dizzykitty3.androidtoolkitty.foundation.const.QR_CODE_GENERATOR_SCREEN

@Preview
@Composable
private fun Preview() {
    val navController = rememberNavController()
    TestCard(navController)
}

@Composable
fun TestCard(navController: NavHostController) {
    CustomCard(title = "Test", icon = Icons.Outlined.Terminal) {
        Button(
            onClick = { navController.navigate(QR_CODE_GENERATOR_SCREEN) }
        ) {
            Text(text = "QR Code generator")
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            Text(text = "QR Code scanner")
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            Text(text = "arrange home card")
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            Text(text = "extract apk(s)")
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            Text(text = "install by apk(s)")
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            Text(text = "jump to activity")
        }
        Button(
            onClick = { /*TODO*/ }
        ) {
            Text(text = "to-do list")
        }
        Text(text = "To-do list")
        Text(text = "SP -> DataStore")
        Text(text = "Date countdown feature")
        Text(text = "Share to clipboard feature")
        Text(text = "Add license info")
        Text(text = "Add @Preview")
    }
}