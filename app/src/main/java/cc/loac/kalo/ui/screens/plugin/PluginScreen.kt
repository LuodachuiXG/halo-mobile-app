package cc.loac.kalo.ui.screens.plugin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.Plugin
import cc.loac.kalo.data.repositories.PluginRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.PluginItemCard
import cc.loac.kalo.ui.components.ShimmerCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PluginScreen(
    navController: NavController,
    pluginViewModel: PluginViewModel = viewModel()
) {
    // 启动时获取所有插件
    LaunchedEffect(Unit) {
        pluginViewModel.getAllPlugins()
    }

    Column {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "插件")
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
        ) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                PluginItems(pluginViewModel)
            }
        }
    }
}

/**
 * 展示所有插件
 */
@Composable
fun PluginItems(
    pluginViewModel: PluginViewModel
) {
    // 插件实体类
    var plugins by remember {
        mutableStateOf(Plugin())
    }

    // 对话框信息
    var showAlertMessage = ""
    // 是否显示对话框
    var showAlert by remember { mutableStateOf(false) }
    if (showAlert) {
        "插件获取失败，$showAlertMessage".Alert {
            showAlert = false
        }
    }

    // 插件信息状态改变
    LaunchedEffect(pluginViewModel.plugins.value) {
        pluginViewModel.plugins.handle(
            success = {
                // 插件信息获取成功
                plugins = it
            },
            failure = {
                showAlertMessage = it
                showAlert = true
            }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (plugins.isEmpty()) {
            // 加载中显示骨架屏
            ShimmerCard()
        } else {
            // 展示所有插件
            LazyColumn {
                items(plugins.items) {
                    PluginItemCard(it)
                }
            }
        }
    }
}

/**
 * 插件页面 ViewModel
 */
class PluginViewModel: ViewModel() {
    // 初始化插件数据操作类
    private val pluginRepo = PluginRepo()

    private val _plugins = mutableStateOf(MyResponse<Plugin>())
    val plugins: State<MyResponse<Plugin>> = _plugins

    /**
     * 获取所有插件
     */
    fun getAllPlugins() {
        viewModelScope.launch {
            _plugins.value = pluginRepo.getAllPlugin()
        }
    }
}