package cc.loac.kalo.network.api

import cc.loac.kalo.data.models.PublicKey
import retrofit2.Response
import retrofit2.http.GET

/**
 * 与登录有关的网络操作请求接口
 */
interface LoginApiService {

    /**
     * 获取 Public-Key
     */
    @GET("/login/public-key")
    suspend fun getPublicKey(): Response<PublicKey>
}