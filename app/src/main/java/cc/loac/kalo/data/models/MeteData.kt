package cc.loac.kalo.data.models

import java.util.Date

/**
 * 元数据实体类
 */
data class MetaData(
    val name: String = "",
    val finalizers: Set<String> = emptySet(),
    val labels: Map<String, String> = emptyMap(),
    val annotations: Map<String, String> = emptyMap(),
    var version: Long = 0,
    val creationTimestamp: Date? = null,
    val deletionTimestamp: Date? = null,
    val generateName: String = ""
) {
    companion object {
        // annotations 的 key，显示的是等于用户的权限名
        const val AUTHORIZATION_NAME = "rbac.authorization.halo.run/display-name"
    }
}