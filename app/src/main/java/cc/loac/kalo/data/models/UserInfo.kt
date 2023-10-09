package cc.loac.kalo.data.models

/**
 * Halo 用户信息实体类
 * 场景：登录 Halo 后请求的用户数据
 */
data class UserInfo(
    val user: User,
    val roles: List<Role>
)

data class User(
    val spec: UserSpec,
    val status: UserStatus,
    val apiVersion: String,
    val kind: String,
    val metadata: MetaData
)

data class Role(
    val rules: List<Rule>,
    val apiVersion: String,
    val kind: String,
    val metadata: MetaData
)

data class Rule(
    val apiGroups: List<String>,
    val resources: List<String>,
    val resourceNames: List<String>,
    val nonResourceURLs: List<String>,
    val verbs: List<String>
)

data class UserSpec(
    val displayName: String,
    val avatar: String,
    val email: String,
    val phone: String,
    val password: String,
    val registeredAt: String,
    val twoFactorAuthEnabled: Boolean,
    val disabled: Boolean
)

data class UserStatus(
    val permalink: String
)