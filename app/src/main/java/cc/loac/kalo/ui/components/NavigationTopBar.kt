package cc.loac.kalo.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import cc.loac.kalo.R
import cc.loac.kalo.ui.theme.LARGE_MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE

/**
 * 带返回按钮的 TopAppBar
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
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "返回",
                    modifier = Modifier.size(LARGE_MIDDLE)
                )
            }
        }
    )
}