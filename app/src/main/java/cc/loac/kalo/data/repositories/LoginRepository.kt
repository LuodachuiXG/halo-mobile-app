package cc.loac.kalo.data.repositories


import android.util.Log
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PublicKey
import cc.loac.kalo.network.RetrofitClient

/**
 * 登录相关的数据操作类
 */
class LoginRepository {
    private val loginApi = RetrofitClient.loginApiService

    /**
     * 获取 Public-Key
     */
    suspend fun getPublicKey(): MyResponse<PublicKey> {
        val result = MyResponse<PublicKey>()
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