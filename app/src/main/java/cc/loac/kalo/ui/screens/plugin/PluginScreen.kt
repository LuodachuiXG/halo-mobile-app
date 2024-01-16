package cc.loac.kalo.ui.screens.plugin

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cc.loac.kalo.R
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.Plugin
import cc.loac.kalo.data.models.PluginItem
import cc.loac.kalo.data.repositories.PluginRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.EmptyContent
import cc.loac.kalo.ui.components.NavigationBackTopBar
import cc.loac.kalo.ui.components.PluginItemCard
import cc.loac.kalo.ui.components.ShimmerCard
import cc.loac.kalo.ui.components.SwitchButton
import cc.loac.kalo.ui.theme.LARGE_MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL
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

    // 对话框内容，不为空就显示对话框
    var dialogText by remember { mutableStateOf("") }

    // 插件实体类，存储获取到的插件
    var plugins by remember {
        mutableStateOf(Plugin())
    }

    // 是否在加载中
    var isLoading by remember { mutableStateOf(false) }

    // 启动时获取所有插件
    LaunchedEffect(Unit) {
        pluginViewModel.getAllPlugins()
    }
    // 插件信息状态改变
    LaunchedEffect(pluginViewModel.plugins.value) {
        pluginViewModel.plugins.handle(
            success = {
                // 插件信息获取成功
                plugins = it
            },
            failure = {
                // 插件信息获取失败，弹出信息框
                dialogText = "插件加载失败，$it"
            }
        )
    }

    // 显示对话框
    if (dialogText.isNotEmpty()) {
        dialogText.Alert {
            dialogText = ""
        }
    }

    // 底部弹出菜单，点击插件后弹出显示插件信息
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    // 弹出菜单显示的插件索引
    var bottomSheetPluginIndex by remember { mutableIntStateOf(0) }
    // 是否显示弹出菜单
    var showBottomSheet by remember { mutableStateOf(false) }

    // 底部弹出菜单中是否显示插件的设置，true 为显示，false 为不显示（显示插件信息）
    var showPluginSetting by remember { mutableStateOf(false) }

    // 检测插件启用状态修改事件
    LaunchedEffect(pluginViewModel.updatePluginInfo.value) {
        pluginViewModel.updatePluginInfo.handle(
            success = {
                // 插件状态启用状态切换成功
                pluginViewModel.getAllPlugins()
            },
            failure = {
                // 插件状态启用状态切换失败，弹出对话框
                dialogText = "操作失败，$it"
            },
            compose = {
                // 取消加载状态
                isLoading = false
            }
        )
    }

    // 脚手架
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
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "返回",
                            modifier = Modifier.size(LARGE_MIDDLE)
                        )
                    }
                },
                actions = {
                    // 提示按钮
                    IconButton(
                        onClick = {
                            // 显示提示信息框
                            dialogText = "插件设置功能仍在测试阶段，并未完全支持所有表单的渲染。\n" +
                                    "如果在进入插件设置的时候闪退，请附带插件名到 GitHub 或 Gitee 提交 Issue，" +
                                    "将尽快修复该问题。"
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.warning),
                            contentDescription = "提示",
                            modifier = Modifier.size(LARGE_MIDDLE)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            // 是否显示弹出菜单
            if (showBottomSheet) {
                // 屏幕底部弹出菜单
                ModalBottomSheet(
                    onDismissRequest = {
                        // 收回弹出菜单
                        showBottomSheet = false
                        // 收回弹出菜单后也要将显示插件设置选项设为 false
                        // 防止出现点击下一个插件时直接显示插件设置
                        showPluginSetting = false
                    },
                    sheetState = sheetState,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    // 底部弹出菜单内容
                    PluginBottomSheet(
                        isLoading = isLoading,
                        showPluginSetting = showPluginSetting,
                        pluginItem = plugins.items!![bottomSheetPluginIndex],
                        onPluginSwitchClick = {
                            // 插件启用状态改变按钮点击事件
                            scope.launch {
                                // 切换插件启用状态
                                // 复制一个当前插件实体类
                                val pluginItem = it.copy(
                                    spec = it.spec.copy(
                                        enabled = !it.spec.enabled
                                    )
                                )
                                // 加载状态
                                isLoading = true
                                // 更新插件启用状态
                                pluginViewModel.updatePluginInfo(pluginItem)
                            }
                        },
                        onPluginSettingClick = {
                            // 插件设置按钮点击事件
                            showPluginSetting = !showPluginSetting
                        }
                    )
                }
            }
            // 展示所有插件
            PluginItems(plugins.items) { index ->
                // 插件点击事件
                // 保存点击的插件索引
                bottomSheetPluginIndex = index
                // 弹出底部菜单
                showBottomSheet = true
            }
        }
    }
}

/**
 * 底部弹出菜单，显示插件信息
 * @param pluginItem 插件信息实体类
 * @param isLoading 当前是否在加载状态（防止在加载中重复操作）
 * @param showPluginSetting 是否显示插件的设置选项
 * @param onPluginSwitchClick 插件启用状态切换按钮点击事件
 * @param onPluginSettingClick 插件设置按钮点击事件
 */
@Composable
private fun PluginBottomSheet(
    pluginItem: PluginItem,
    isLoading: Boolean,
    showPluginSetting: Boolean,
    onPluginSwitchClick: (PluginItem) -> Unit = {},
    onPluginSettingClick: (PluginItem) -> Unit = {}
) {
    Column (
        modifier = Modifier
            .padding(horizontal = SMALL)
    ) {
        // 插件卡片
        PluginItemCard(
            pluginItem = pluginItem,
            onClick = {}
        )

        // 插件详细信息 / 插件设置选项
        Box (
            modifier = Modifier
                .padding(bottom = SMALL)
        ) {
            Column (
                modifier = Modifier.animateContentSize()
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SMALL)
                ) {
                    // 插件启用状态切换按钮
                    SwitchButton(
                        enable = pluginItem.spec.enabled,
                        enableText = "禁用",
                        disableText = "启用",
                        isLoading = isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = VERY_SMALL)
                    ) {
                        onPluginSwitchClick(pluginItem)
                    }

                    // 插件设置按钮
                    OutlinedCard (
                        modifier = Modifier
                            .weight(1f)
                            .clip(CardDefaults.shape)
                            .clickable { onPluginSettingClick(pluginItem) }
                    ) {
                        Text(
                            text = if (showPluginSetting) "返回" else "设置",
                            modifier = Modifier
                                .padding(SMALL)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                // 根据 showPluginSetting 来判断当前是显示插件信息还是设置选项
                when (showPluginSetting) {
                    true -> PluginSetting(
                        pluginName = pluginItem.metadata.name
                    )
                    false -> PluginInfo(pluginItem = pluginItem)
                }
            }
        }
    }
}

/**
 * 展示插件信息
 * @param pluginItem 插件实体类
 */
@Composable
fun PluginInfo(
    pluginItem: PluginItem,
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
        "协议" to (pluginItem.spec.license.firstOrNull()?.name ?: "未知"),
        "最后一次启动" to pluginItem.status.lastStartTime.formatString()
    )


    Column (
        modifier = Modifier.fillMaxHeight()
    ) {
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

/**
 * 展示所有插件
 * @param pluginItems 插件集合
 * @param onPluginClick 插件点击事件，回调点击的插件索引
 */
@Composable
private fun PluginItems(
    pluginItems: List<PluginItem>?,
    onPluginClick: (Int) -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(SMALL)
            .animateContentSize()
    ) {
        if (pluginItems == null) {
            // 加载中显示骨架屏
            ShimmerCard()
        } else if (pluginItems.isEmpty()) {
            // 插件为空
            EmptyContent(Modifier.fillMaxSize())
        } else {
            // 展示所有插件
            LazyColumn {
                itemsIndexed(pluginItems) { index, item ->
                    PluginItemCard(
                        pluginItem = item,
                        modifier = Modifier.padding(bottom = SMALL),
                        onClick = { _ ->
                            onPluginClick(index)
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

    // 获取所有插件状态
    private val _plugins = mutableStateOf(MyResponse<Plugin>())
    val plugins: State<MyResponse<Plugin>> = _plugins

    // 修改插件信息状态
    private val _updatePluginInfo = mutableStateOf(MyResponse<PluginItem>())
    val updatePluginInfo: State<MyResponse<PluginItem>> = _updatePluginInfo

    /**
     * 获取所有插件
     */
    fun getAllPlugins() {
        viewModelScope.launch {
            _plugins.value = pluginRepo.getAllPlugin()
        }
    }

    /**
     * 更新插件信息
     */
    fun updatePluginInfo(
        pluginItem: PluginItem
    ) {
        viewModelScope.launch {
            _updatePluginInfo.value = pluginRepo.updatePluginInfo(pluginItem)
        }
    }
}