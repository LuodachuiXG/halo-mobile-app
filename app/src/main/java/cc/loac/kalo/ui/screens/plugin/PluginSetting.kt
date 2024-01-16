package cc.loac.kalo.ui.screens.plugin

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.loac.kalo.R
import cc.loac.kalo.data.models.FormKit
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PluginSetting
import cc.loac.kalo.data.models.PluginSettingFormSchema
import cc.loac.kalo.data.models.PluginSettingSubmit
import cc.loac.kalo.data.models.PluginSettingSubmitData
import cc.loac.kalo.data.repositories.PluginRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.EmptyContent
import cc.loac.kalo.ui.components.ShimmerCard
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL
import cc.loac.kalo.utils.formatURL
import cc.loac.kalo.utils.toJson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

@Composable
fun PluginSetting(
    pluginName: String,
    vm: PluginSettingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        // 读取插件设置信息
        vm.getPluginSetting(pluginName)
    }

    // 显示对话框
    if (vm.dialogText.value.isNotEmpty()) {
        vm.dialogText.value.Alert {
            vm.dialogText.value = ""
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
                vm.dialogText.value = "插件设置加载失败，$it"
            }
        )
    }

    Column {
        if (pluginSetting.isEmpty()) {
            // 插件设置信息实体类为空，正在加载中，显示骨架屏
            ShimmerCard()
        } else if (pluginSetting.spec!!.forms.isEmpty()) {
            // 插件设置信息实体类不为空，但是设置列表为空
            // 表明当前插件没有设置选项
            EmptyContent(
                text = "当前插件没有设置选项",
                modifier = Modifier.height(150.dp)
            )
        } else {
            // 显示插件设置选项
            PluginSettingItem(vm, pluginSetting)
        }
    }
}


@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun PluginSettingItem(
    vm: PluginSettingViewModel,
    pluginSetting: PluginSetting
) {
    // 所有设置页
    val settings = pluginSetting.spec?.forms
    // 当前设置页
    var current by remember {
        mutableIntStateOf(0)
    }

    TabRow(selectedTabIndex = current) {
        settings!!.forEachIndexed { index, pluginSetting ->
            Tab(
                selected = current == index,
                onClick = { current = index },
                text = {
                    Text(
                        text = pluginSetting.label,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
    // 展示插件设置选项
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PluginSettingForm(vm, settings!![current].formSchema)
    }
}

/**
 * 将插件的设置数据设置到 ViewModel 中
 * @param vm ViewModel [PluginSettingViewModel]
 * @param formSchema 插件设置表单集合
 */
fun setPluginSettingValuesToVM(
    vm: PluginSettingViewModel,
    formSchema: List<PluginSettingFormSchema>
) {
    val list = mutableListOf<String>()
    formSchema.forEach {
        list.add(it.value ?: "")
    }
    vm.setPluginSettingValues(list)
}

/**
 * 渲染插件设置选项
 */
@Composable
fun PluginSettingForm(
    vm: PluginSettingViewModel,
    formSchema: List<PluginSettingFormSchema>
) {
    LaunchedEffect(Unit) {
        // 首先设置插件的设置项数据到 ViewModel 中
        setPluginSettingValuesToVM(vm, formSchema)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = SMALL)
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            )
    ) {
        formSchema.forEachIndexed { index, formSchema ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MIDDLE_MIDDLE)
            ) {
                // 显示对应组件
                when (formSchema.formkit) {
                    FormKit.TEXT, FormKit.TEXTAREA -> {
                        OutlinedTextField(
                            value = vm.getPluginSettingValue(index),
                            onValueChange = { newValue ->
                                vm.setPluginSettingValue(index, newValue)
                            },
                            label = {
                                Text(text = formSchema.label)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CardDefaults.shape
                        )
                    }

                    FormKit.SELECT -> {
                        // 是否显示下拉菜单
                        var showDropMenu by remember {
                            mutableStateOf(false)
                        }
                        OutlinedTextField(
                            value = vm.getPluginSettingValue(index),
                            onValueChange = {},
                            label = {
                                Text(text = formSchema.label)
                            },
                            readOnly = true,
                            trailingIcon = {
                                // 下拉菜单按钮
                                IconButton(
                                    onClick = {
                                        showDropMenu = true
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_down),
                                        contentDescription = "显示下拉菜单",
                                        modifier = Modifier.size(MIDDLE)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CardDefaults.shape
                        )

                        // 选项下拉菜单
                        DropdownMenu(
                            expanded = showDropMenu,
                            onDismissRequest = { showDropMenu = false }
                        ) {
                            formSchema.options.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = option.label)
                                    },
                                    onClick = {
                                        vm.setPluginSettingValue(index, option.value)
                                        showDropMenu = false
                                    }
                                )
                            }
                        }
                    }

                    FormKit.URL -> {
                        // 标记当前用户输入的是否是正确的 URL
                        var isCorrectUrl by remember {
                            mutableStateOf(true)
                        }
                        OutlinedTextField(
                            value = vm.getPluginSettingValue(index),
                            onValueChange = {
                                vm.setPluginSettingValue(index, it)
                                // 判断输入的是否是正确的 URL
                                isCorrectUrl = it.formatURL().isNotEmpty() || it.isEmpty()
                            },
                            isError = !isCorrectUrl,
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(text = formSchema.label)
                            },
                            shape = CardDefaults.shape
                        )
                        if (!isCorrectUrl) {
                            Text(
                                text = "请输入正确的地址",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = VERY_SMALL),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                // 显示帮助
                if (formSchema.help != null) {
                    Text(
                        text = formSchema.help,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = VERY_SMALL)
                    )
                }
            }
        }

        // 操作按钮
        Row (
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    // 重新将插件原来的设置数据设置到 ViewModel 中
                    setPluginSettingValuesToVM(vm, formSchema)
                },
                shape = CardDefaults.shape,
                modifier = Modifier.padding(end = SMALL)
            ) {
                Text(text = "重置")
            }

            // 保存设置按钮
            OutlinedButton(
                onClick = {
                    vm.updatePluginSetting(formSchema)
                },
                shape = CardDefaults.shape
            ) {
                Text(text = "保存设置")
            }
        }
    }
}


/**
 * 插件设置页面 ViewModel
 */
class PluginSettingViewModel : ViewModel() {
    // 初始化插件数据操作类
    private val pluginRepo = PluginRepo()

    // 对话框内容，不为空就显示对话框
    val dialogText = mutableStateOf("")

    // 插件设置实体类
    private val _pluginSetting = mutableStateOf(MyResponse<PluginSetting>())
    val pluginSetting: State<MyResponse<PluginSetting>> = _pluginSetting

    // 插件设置选项的数据
    private val _pluginSettingValues = mutableStateListOf<String>()

    /**
     * 设置全部插件设置项数据
     * @param values 数据集合
     */
    fun setPluginSettingValues(values: List<String>) {
        _pluginSettingValues.clear()
        _pluginSettingValues.addAll(values)
    }

    /**
     * 设置某个插件设置项数据
     * @param index 插件索引
     * @param value 要设置的数据
     */
    fun setPluginSettingValue(index: Int, value: String) {
        _pluginSettingValues[index] = value
    }

    /**
     * 获取某个插件设置项数据
     * @param index 插件索引
     */
    fun getPluginSettingValue(index: Int): String {
        return if (index < 0 || index >= _pluginSettingValues.size) {
            ""
        } else {
            _pluginSettingValues[index]
        }
    }

    /**
     * 更新插件设置数据
     * @param formSchemas 当前插件设置实体类集合
     */
    fun updatePluginSetting(
        formSchemas: List<PluginSettingFormSchema>,
    ) {
        viewModelScope.launch {
            // 将插件设置项和数据封装成 JSON
            val jsonData = JsonObject()
            formSchemas.forEachIndexed { index, formSchema ->
                jsonData.addProperty(formSchema.name, getPluginSettingValue(index))
            }
            // 封装插件更新提交实体类
            val metaData = _pluginSetting.value.data!!.metadata
            val newData = PluginSettingSubmit(
                data = PluginSettingSubmitData(
                    basic = jsonData.toString()
                ),
                apiVersion = _pluginSetting.value.data!!.apiVersion,
                kind = "ConfigMap",
                metadata = metaData.copy(
                    name = metaData.name.replace("settings", "configMap")
                )
            )
            Log.e(this.javaClass.name, newData.toJson())
        }
    }

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