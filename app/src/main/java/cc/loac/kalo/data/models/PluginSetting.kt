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
    val formkit: String,
    val label: String,
    val name: String,
    val validation: String,
    var value: String,
    val help: String,
    val options: List<PluginSettingFormOption>
)

data class PluginSettingFormOption(
    val label: String,
    val value: String
)
