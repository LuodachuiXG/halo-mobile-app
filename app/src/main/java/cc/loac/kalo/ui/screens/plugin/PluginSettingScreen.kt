package cc.loac.kalo.ui.screens.plugin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.loac.kalo.ui.components.NavigationBackTopBar

@Composable
fun PluginSettingScreen(
    navController: NavController,
    pluginName: String,
    displayName: String,
    viewModel: PluginSettingViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            NavigationBackTopBar(
                navController = navController,
                title = displayName
            )
        }
    ) { padding ->
        Column (
            modifier = Modifier.padding(padding)
        ) {

        }
    }
}


/**
 * 插件设置页面 ViewModel
 */
class PluginSettingViewModel: ViewModel() {

}