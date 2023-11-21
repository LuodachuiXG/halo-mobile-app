package cc.loac.kalo.network.api

import cc.loac.kalo.data.models.Plugin
import retrofit2.Response
import retrofit2.http.GET

/**
 * 插件相关网络请求接口
 */
interface PluginApiService {
    @GET("/apis/api.console.halo.run/v1alpha1/plugins")
    suspend fun getAllPlugins(): Response<Plugin>
}