package cc.loac.kalo.network

import androidx.compose.runtime.State
import cc.loac.kalo.data.models.ErrorResponse
import cc.loac.kalo.data.models.MyResponse
import com.google.gson.Gson
import retrofit2.Response

/**
 * 网络请求处理方法
 * @param success 请求成功，回调 T
 * @param failure 请求失败，回调 ErrorResponse
 * @see ErrorResponse Halo 站点网络请求异常类
 */
fun <T> Response<T>.handle(
    success: (T, Response<T>) -> Unit,
    failure: (ErrorResponse) -> Unit
) {
    if (this.isSuccessful) {
        success(this.body()!!, this)
    } else {
        try {
            val gson = Gson()
            val errorResponse =
                gson.fromJson(this.errorBody()?.string(), ErrorResponse::class.java)
            failure(errorResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            failure(ErrorResponse(detail = "未知错误"))
        }
    }
}

/**
 * 状态处理方法，一般用于对 ViewModel 中数据变化做出响应
 * @param success 请求成功，回调 T
 * @param failure 请求失败，回调错误信息
 * @param compose 成功与失败都会触发回调
 */
fun <T> State<MyResponse<T>>.handle(
    success: (T) -> Unit,
    failure: (errMsg: String) -> Unit,
    compose: () -> Unit = {}
) {
    if (this.value.isNotNone()) {
            if (this.value.isSuccessful()) {
            val data = this.value.data
            if (data == null) {
                failure("响应数据为空")
                compose()
                return
            }
            success(data)
        } else {
            failure(this.value.errMsg)
        }
        compose()
    }
}
