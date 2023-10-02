package cc.loac.kalo.network.api

import cc.loac.kalo.data.models.PublicKey
import cc.loac.kalo.data.models.UserLogin
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * 与登录有关的网络操作请求接口
 */
interface LoginApiService {

    /**
     * 获取 Public-Key
     */
    @GET("/login/public-key")
    suspend fun getPublicKey(): Response<PublicKey>

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/login")
    @Headers(
        "accept: application/json, text/plain, */*",
        "accept-language: zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7"
    )
    suspend fun login(
        @Field("_csrf") csrf: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<UserLogin>
}