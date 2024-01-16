package cc.loac.kalo.data.models

import com.google.gson.annotations.SerializedName

/**
 * 插件设置实体类
 * 场景：获取插件的设置参数
 */
data class PluginSetting(
    val spec: PluginSettingForms? = null,
    val apiVersion: String = "",
    val kind: String = "",
    val metadata: MetaData = MetaData()
) {
    /**
     * 实体类是否为空
     */
    fun isEmpty(): Boolean {
        return spec == null &&
                apiVersion.isEmpty() &&
                kind.isEmpty() &&
                metadata == MetaData()
    }
}

data class PluginSettingForms(
    val forms: List<PluginSettingForm> = emptyList()
)

data class PluginSettingForm(
    val group: String,
    val label: String,
    val formSchema: List<PluginSettingFormSchema>
)

data class PluginSettingFormSchema(
    @SerializedName("\$formkit")
    val formkit: FormKit,
    val label: String,
    val name: String,
    val validation: String,
    var value: String?,
    val help: String?,
    val options: List<PluginSettingFormOption>
)

data class PluginSettingFormOption(
    val label: String,
    val value: String
)

/**
 * 插件设置数据提交更新实体类
 */
data class PluginSettingSubmit(
    val data: PluginSettingSubmitData,
    val apiVersion: String = "",
    val kind: String = "",
    val metadata: MetaData = MetaData()
)

data class PluginSettingSubmitData(
    val basic: String
)

/**
 * 表单组件类型枚举类
 */
enum class FormKit {
    @SerializedName("textarea")
    TEXTAREA,
    @SerializedName("select")
    SELECT,
    @SerializedName("text")
    TEXT,
    @SerializedName("url")
    URL
}
