package cc.loac.kalo.data.models

import androidx.compose.runtime.MutableState

/**
 * Halo 用户信息实体类
 * 场景：登录 Halo 后请求的用户数据
 */
data class UserInfo(
    val user: User = User(),
    val roles: List<Role> = listOf()
)

/**
 * 判断用户信息实体类是否为空
 */
fun UserInfo.isEmpty(): Boolean {
    return this.user == User() && this.roles.isEmpty()
}

/**
 * 判断用户信息实体类是否不为空
 */
fun UserInfo.isNotEmpty(): Boolean {
    return !this.isEmpty()
}

data class User(
    val spec: UserSpec = UserSpec(),
    val status: UserStatus = UserStatus(),
    val apiVersion: String = "",
    val kind: String = "",
    val metadata: MetaData = MetaData()
)

data class Role(
    val rules: List<Rule> = listOf(),
    val apiVersion: String = "",
    val kind: String = "",
    val metadata: MetaData = MetaData()
)

data class Rule(
    val apiGroups: List<String> = listOf(),
    val resources: List<String> = listOf(),
    val resourceNames: List<String> = listOf(),
    val nonResourceURLs: List<String> = listOf(),
    val verbs: List<String> = listOf()
)

data class UserSpec(
    val displayName: String = "",
    var avatar: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val registeredAt: String = "",
    val twoFactorAuthEnabled: Boolean = false,
    val disabled: Boolean = false
)

data class UserStatus(
    val permalink: String = ""
)