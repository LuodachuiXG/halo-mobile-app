package cc.loac.kalo.data.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * 网络请求类
 * @param status 请求状态
 * @param data 请求的数据
 * @param errMsg 错误信息
 * @param code 请求代码
 */
data class MyResponse<T> (
    var status: Status = Status.NONE,
    var data: T? = null,
    var errMsg: String = "",
    var code: Int = 0) {

    /**
     * 设置请求成功
     * @param response 请求实体
     */
    fun success(response: Response<T>): MyResponse<T> {
        status = Status.SUCCESS
        data = response.body()
        code = response.code()
        return this
    }

    /**
     * 设置请求失败
     * @param msg 失败信息
     */
    fun failure(msg: String): MyResponse<T> {
        status = Status.FAILURE
        errMsg = msg
        return this
    }

    /**
     * 设置请求失败
     * @param response 请求实体
     */
    fun failure(response: Response<T>): MyResponse<T> {
        val msg = response.errorBody()?.string() ?: "未知错误"
        code = response.code()
        return failure(msg)
    }

    /**
     * 请求是否成功
     */
    fun isSuccessful(): Boolean {
        return status == Status.SUCCESS
    }

    /**
     * 请求状态是否是 None
     */
    fun isNotNone(): Boolean {
        return status != Status.NONE
    }


    /**
     * 清空数据
     */
    fun clear() {
        status = Status.NONE
        data = null
        errMsg = ""
        code = 0
    }
}


/**
 * 请求枚举类
 */
enum class Status {
    SUCCESS,
    FAILURE,
    NONE
}