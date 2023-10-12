package cc.loac.kalo.data.models

/**
 * 元数据实体类
 */
data class MetaData(
    val name: String = "",
    val finalizers: List<String> = listOf(),
    val labels: Map<String, String> = mapOf(),
    val annotations: Map<String, String> = mapOf(),
    val version: Int = 0,
    val creationTimestamp: String = ""
)