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
    // annotations 的 key，用户的权限名
    private val _authorizationName = "rbac.authorization.halo.run/display-name"

    /**
     * 获取用户的权限名，该权限名存储在用户信息的元数据中
     * @see User
     */
    fun getUserRoles(): String {
        return annotations[_authorizationName] ?: ""
    }
}