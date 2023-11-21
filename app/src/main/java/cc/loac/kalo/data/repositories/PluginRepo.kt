package cc.loac.kalo.data.repositories

import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.Plugin
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.PluginApiService
import cc.loac.kalo.network.api.UserApiService
import cc.loac.kalo.network.handle

class PluginRepo {

    private val url: String = ConfigRepo.get(ConfigKey.HALO_URL)
    private var pluginApi: PluginApiService = RetrofitClient.create().pluginApiService

    /**
     * 获取所有插件
     */
    suspend fun getAllPlugin(): MyResponse<Plugin> {
        val result = MyResponse<Plugin>()
        try {
            pluginApi.getAllPlugins().handle(
                success = { plugin, _ ->
                    plugin.items.forEach {
                        // 如果插件 Logo 是相对地址就切换为绝对地址
                        var logoURL = it.status.logo
                        if (!logoURL.contains("http") &&
                            !logoURL.contains("data:image")) {
                            logoURL = "$url$logoURL"
                        }
                        it.status.logo = logoURL
                    }
                    result.success(plugin)
                },
                failure = {
                    result.failure(it.detail)
                }
            )
        } catch (e: Exception) {
            result.failure(e.message.toString())
            e.printStackTrace()
        }
        return result
    }
}