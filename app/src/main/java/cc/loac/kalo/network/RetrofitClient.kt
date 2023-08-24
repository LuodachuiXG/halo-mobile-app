package cc.loac.kalo.network

import cc.loac.kalo.network.api.LoginApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 网络请求类
 */
object RetrofitClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://loac.cc")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val loginApiService: LoginApiService = retrofit.create(LoginApiService::class.java)
}