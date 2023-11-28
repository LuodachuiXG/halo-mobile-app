package cc.loac.kalo.data.models

import java.util.Date

/**
 * 插件实体类
 * 场景：获取安装的插件
 */
data class Plugin(
    val page: Int = 0,
    val size: Int = 0,
    val total: Int = 0,
    val items: List<PluginItem>? = null,
    val first: Boolean = false,
    val last: Boolean = false,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val totalPages: Int = 0
) {
    /**
     * 判断实体类是否为空
     */
    fun isEmpty(): Boolean {
        return page == 0 && size == 0 && total == 0 && items == null &&
                !first && !last && !hasNext &&
                !hasPrevious && totalPages == 0
    }
}

data class PluginItem(
    val spec: PluginItemSpec = PluginItemSpec(),
    val status: PluginItemStatus = PluginItemStatus(),
    val metadata: MetaData = MetaData(),
    val apiVersion: String = "",
    val kind: String = ""
) {
    /**
     * 判断实体类是否为空
     */
    fun isEmpty(): Boolean {
        return spec == PluginItemSpec() &&
                status == PluginItemStatus() &&
                metadata == MetaData()
    }
}

data class PluginItemSpec(
    val displayName: String = "",
    val version: String = "",
    val author: PluginAuthor = PluginAuthor(),
    var logo: String = "",
    val pluginDependencies: Map<String, String> = emptyMap(),
    val homepage: String = "",
    val description: String = "",
    val license: List<PluginLicense> = emptyList(),
    val requires: String = "",
    var enabled: Boolean = false,
    val settingName: String = "",
    val configMapName: String = ""
)

data class PluginAuthor(
    val name: String = "",
    val website: String = ""
)

data class PluginLicense(
    val name: String = "",
    val url: String = ""
)

data class PluginItemStatus(
    var logo: String = "",
    val lastStartTime: Date = Date(0),
    val loadLocation: String = "",
    val phase: String = "",
    val conditions: List<PluginCondition> = emptyList()
)

data class PluginCondition(
    val type: String = "",
    val status: String = "",
    val lastTransitionTime: String = "",
    val message: String = "",
    val reason: String = ""
)