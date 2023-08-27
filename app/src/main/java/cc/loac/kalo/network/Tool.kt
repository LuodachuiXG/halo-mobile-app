package cc.loac.kalo.network

import cc.loac.kalo.data.models.ErrorResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

/**
 * 网络请求处理方法
 * @param success 请求成功，回调 T
 * @param failure 请求失败，回调 ErrorResponse
 * @see ErrorResponse Halo 站点网络请求异常类
 */
fun <T> Response<T>.handle(
    success: (T) -> Unit,
    failure: (ErrorResponse) -> Unit
) {
    if (this.isSuccessful) {
        success(this.body()!!)
    } else {
        val gson = Gson()
        val errorResponse =
            gson.fromJson(this.errorBody()?.string(), ErrorResponse::class.java)
        failure(errorResponse)
    }
}
