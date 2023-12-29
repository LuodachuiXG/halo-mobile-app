package cc.loac.kalo.ui.screens.plugin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PluginSetting
import cc.loac.kalo.data.repositories.PluginRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.EmptyContent
import cc.loac.kalo.ui.components.NavigationBackTopBar
import cc.loac.kalo.ui.components.ShimmerCard
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.utils.toast
import kotlinx.coroutines.launch

@Composable
fun PluginSettingScreen(
    navController: NavController,
    pluginName: String,
    displayName: String,
    vm: PluginSettingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        // 插件名为空，弹回上一页
        if (pluginName.isEmpty()) {
            "插件名为空".toast()
            navController.navigateUp()
            return@LaunchedEffect
        }

        // 读取插件设置信息
        vm.getPluginSetting(pluginName)
    }

    // 对话框内容，不为空就显示对话框
    var dialogText by remember {
        mutableStateOf("")
    }

    // 显示对话框
    if (dialogText.isNotEmpty()) {
        dialogText.Alert {
            dialogText = ""
        }
    }

    // 插件设置信息实体类
    var pluginSetting by remember {
        mutableStateOf(PluginSetting())
    }

    // 插件设置信息状态改变，从服务器得到数据
    LaunchedEffect(vm.pluginSetting.value) {
        vm.pluginSetting.handle(
            success = {
                // 插件设置信息获取成功
                pluginSetting = it
            },
            failure = {
                // 插件设置信息获取失败
                dialogText = "插件设置加载失败，$it"
            }
        )
    }

    Scaffold(
        topBar = {
            NavigationBackTopBar(
                navController = navController,
                title = displayName
            )
        }
    ) { padding ->
        Column (
            modifier = Modifier
                .padding(padding)
                .padding(SMALL)
        ) {
            if (pluginSetting.isEmpty()) {
                // 插件设置信息实体类为空，正在加载中，显示骨架屏
                ShimmerCard()
            } else if (pluginSetting.spec!!.forms.isEmpty()) {
                // 插件设置信息实体类不为空，但是设置列表为空
                // 表明当前插件没有设置选项
                EmptyContent(text = "当前插件没有设置选项")
            }
        }
    }
}


/**
 * 插件设置页面 ViewModel
 */
class PluginSettingViewModel: ViewModel() {
    // 初始化插件数据操作类
    private val pluginRepo = PluginRepo()

    // 插件设置实体类
    private val _pluginSetting = mutableStateOf(MyResponse<PluginSetting>())
    val pluginSetting: State<MyResponse<PluginSetting>> = _pluginSetting


    /**
     * 获取插件设置信息
     * @param pluginName 插件名
     */
    fun getPluginSetting(pluginName: String) {
        viewModelScope.launch {
            _pluginSetting.value = pluginRepo.getPluginSetting(pluginName)
        }
    }
}