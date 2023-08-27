package cc.loac.kalo.data.models

/**
 * 用户登录请求服务器返回的数据实体类
 */
data class UserLogin(
    val username: String,
    val authorities: List<Map<String, String>>,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean
)