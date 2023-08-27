package cc.loac.kalo.data.models

/**
 * 网络请求异常实体类
 */
data class ErrorResponse(
    val type: String = "",
    val title: String = "",
    val status: Int = 0,
    val detail: String = "",
    val instance: String = "",
    val requestId: String = "",
    val timestamp: String = ""
)