package cc.loac.kalo.data.models

import retrofit2.Response

/**
 * 请求类
 * @param status 请求状态
 * @param data 请求的数据
 * @param errMsg 错误信息
 * @param code 请求代码
 */
data class MyResponse<T> (
    var status: Status = Status.NONE,
    var data: T? = null,
    var errMsg: String = "",
    var code: Int = 0
) {

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
     * 设置请求成功
     */
    fun success(data: T): MyResponse<T> {
        status = Status.SUCCESS
        this.data = data
        return this
    }

    fun success(): MyResponse<T> {
        status = Status.SUCCESS
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
        val result = (status == Status.SUCCESS)
        // 状态改成 None
        // 这里一般用于判断是否请求成功
        // 所以这里判断后就修改 Status
        status = Status.NONE
        return result
    }

    /**
     * 请求状态是否是 None
     */
    fun isNotNone(): Boolean {
        return status != Status.NONE
    }


    /**
     * 清空数据
     * 有些地方是通过状态改变来触发事件
     * 所有可能需要通过清除数据来使
     * 状态改变时再次成功触发事件？
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