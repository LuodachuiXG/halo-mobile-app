package cc.loac.kalo.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Home 页面
 */
@Composable
fun HomeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Home",
                modifier = Modifier.padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 20.dp,
                    bottom = 20.dp
                )
            )
        }
    }
}