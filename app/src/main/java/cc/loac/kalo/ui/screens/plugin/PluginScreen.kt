package cc.loac.kalo.ui.screens.plugin


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.Plugin
import cc.loac.kalo.data.models.PluginItem
import cc.loac.kalo.data.repositories.PluginRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.PluginItemCard
import cc.loac.kalo.ui.components.ShimmerCard
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.utils.formatString
import cc.loac.kalo.utils.toast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginScreen(
    navController: NavController,
    pluginViewModel: PluginViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    // 启动时获取所有插件
    LaunchedEffect(Unit) {
        pluginViewModel.getAllPlugins()
    }

    // 底部弹出菜单，点击插件后弹出显示插件信息
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    // 弹出菜单显示的插件信息
    var pluginItem = PluginItem()
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
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                ) {
                    PluginBottomSheet(pluginItem)
                }
            }
            PluginItems(pluginViewModel) {
                scope.launch {
                    pluginItem = it
                    showBottomSheet = true
                }
            }
        }
    }
}

/**
 * 底部弹出菜单，显示插件信息
 */
@Composable
private fun PluginBottomSheet(
    pluginItem: PluginItem
) {
    // 剪贴板管理器
    val clipBoard = LocalClipboardManager.current

    // 插件信息
    val pluginInfo = mapOf (
        "名称" to pluginItem.spec.displayName,
        "描述" to pluginItem.spec.description,
        "版本" to pluginItem.spec.version,
        "Halo 版本要求" to pluginItem.spec.requires,
        "提供方" to pluginItem.spec.author.name,
        "协议" to pluginItem.spec.license.first().name,
        "最后一次启动" to pluginItem.status.lastStartTime.formatString()
    )

    Column (
        modifier = Modifier
            .padding(horizontal = SMALL)
    ) {
        // 插件卡片
        PluginItemCard(
            pluginItem = pluginItem,
            onClick = {}
        )

        // 插件详细信息
        Row (
            modifier = Modifier
                .padding(vertical = SMALL)
        ) {
            Column {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SMALL)
                ) {

                }


                pluginInfo.forEach {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = SMALL)
                            .clip(CardDefaults.shape)
                            .clickable {
                                clipBoard.setText(AnnotatedString(it.value))
                                "${it.key}已复制".toast()
                            }
                    ) {
                        Column (
                            modifier = Modifier.padding(SMALL)
                        ) {
                            Text(
                                text = it.key,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = it.value,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * 底部弹出菜单中插件启动状态按钮
 */
@Composable
private fun PluginBottomSheetSwitchButton(
    pluginItem: PluginItem
) {

}


/**
 * 展示所有插件
 */
@Composable
private fun PluginItems(
    pluginViewModel: PluginViewModel,
    onPluginClick: (PluginItem) -> Unit
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
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(SMALL)
    ) {
        if (plugins.isEmpty()) {
            // 加载中显示骨架屏
            ShimmerCard(Modifier.padding(bottom = SMALL))
        } else {
            // 展示所有插件
            LazyColumn {
                items(plugins.items) {
                    PluginItemCard(
                        pluginItem = it,
                        modifier = Modifier.padding(bottom = SMALL),
                        onClick = { _ ->
                            onPluginClick(it)
                        }
                    )
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