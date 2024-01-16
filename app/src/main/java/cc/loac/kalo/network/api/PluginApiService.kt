package cc.loac.kalo.network.api

import cc.loac.kalo.data.models.Plugin
import cc.loac.kalo.data.models.PluginItem
import cc.loac.kalo.data.models.PluginSetting
import cc.loac.kalo.data.models.PluginSettingSubmit
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 插件相关网络请求接口
 */
interface PluginApiService {
    /**
     * 获取所有插件
     */
    @GET("/apis/api.console.halo.run/v1alpha1/plugins")
    suspend fun getAllPlugins(): Response<Plugin>

    /**
     * 获取插件设置
     * @param pluginName 插件名
     */
    @GET("/apis/api.console.halo.run/v1alpha1/plugins/{pluginName}/setting")
    suspend fun getPluginSetting(
        @Path("pluginName") pluginName: String
    ): Response<PluginSetting>


    /**
     * 更新插件信息
     * @param pluginName 插件名
     * @param pluginItem 插件实体类
     */
    @PUT("/apis/plugin.halo.run/v1alpha1/plugins/{pluginName}")
    suspend fun updatePluginInfo(
        @Path("pluginName") pluginName: String,
        @Body pluginItem: PluginItem
    ): Response<PluginItem>

    /**
     * 更新插件设置信息
     * @param pluginName 插件名
     * @param data 插件设置信息提交修改实体类
     */
    @PUT("/apis/api.console.halo.run/v1alpha1/plugins/{pluginName}/config")
    suspend fun updatePluginSetting(
        @Path("pluginName") pluginName: String,
        @Body data: PluginSettingSubmit
    ): Response<PluginSettingSubmit>
}