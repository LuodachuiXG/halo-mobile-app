package cc.loac.kalo.network.api

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 与用户有关的网络请求接口
 */
interface UserApiService {

    @GET("/apis/api.console.halo.run/v1alpha1/users/-")
    suspend fun getUserProfile(
        @Query("X-Xsrf-Token") token: String
    ): ResponseBody
}