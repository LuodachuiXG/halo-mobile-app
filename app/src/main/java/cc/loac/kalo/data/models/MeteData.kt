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
) {
    companion object {
        // annotations 的 key，显示的是等于用户的权限名
        const val AUTHORIZATION_NAME = "rbac.authorization.halo.run/display-name"
    }
}