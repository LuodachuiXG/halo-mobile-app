package cc.loac.kalo.data.repositories


import android.util.Log
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PublicKey
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.LoginApiService

/**
 * 登录相关的数据操作类
 */
class LoginRepo {
    private lateinit var loginApi: LoginApiService

    /**
     * 获取 Public-Key
     */
    suspend fun getPublicKey(): MyResponse<PublicKey> {
        val result = MyResponse<PublicKey>()

        // 在获取 Public-Key 时顺便初始化 RetrofitClient，因为此时站点地址才输入，不然会闪退
        loginApi = RetrofitClient.getInstance().loginApiService

        return try {
            val response = loginApi.getPublicKey()
            if (response.isSuccessful) {
                result.success(response)
            } else {
                result.failure(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result.failure(e.message.toString())
        }
    }
}