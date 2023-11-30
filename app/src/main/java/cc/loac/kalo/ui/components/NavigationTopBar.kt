package cc.loac.kalo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * 返回导航栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBackTopBar(
    navController: NavController,
    title: String
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "返回"
                )
            }
        }
    )
}