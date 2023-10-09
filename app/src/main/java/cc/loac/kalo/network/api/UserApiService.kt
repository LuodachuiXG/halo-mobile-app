package cc.loac.kalo.network.api

import cc.loac.kalo.data.models.UserInfo
import retrofit2.Response
import retrofit2.http.GET


/**
 * 与用户有关的网络请求接口
 */
interface UserApiService {

    @GET("/apis/api.console.halo.run/v1alpha1/users/-")
    suspend fun getUserProfile(): Response<UserInfo>
}