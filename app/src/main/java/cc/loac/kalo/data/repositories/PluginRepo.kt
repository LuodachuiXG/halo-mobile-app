package cc.loac.kalo.data.repositories

import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.Plugin
import cc.loac.kalo.data.models.PluginItem
import cc.loac.kalo.data.models.PluginSetting
import cc.loac.kalo.data.models.PluginSettingForms
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.PluginApiService
import cc.loac.kalo.network.handle
import java.io.EOFException

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
                    plugin.items?.forEach {
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


    /**
     * 更新插件信息
     * @param pluginItem 插件信息实体类 [PluginItem]
     */
    suspend fun updatePluginInfo(
        pluginItem: PluginItem
    ): MyResponse<PluginItem> {
        val result = MyResponse<PluginItem>()
        try {
            pluginApi.updatePluginInfo(pluginItem.metadata.name, pluginItem).handle(
                success = { plugin, _ ->
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

    /**
     * 获取插件设置信息
     * @param pluginName 插件名
     */
    suspend fun getPluginSetting(
        pluginName: String
    ): MyResponse<PluginSetting> {
        val result = MyResponse<PluginSetting>()
        try {
            pluginApi.getPluginSetting(pluginName).handle(
                success = { pluginSetting, _ ->
                    result.success(pluginSetting)
                },
                failure = {
                    result.failure(it.detail)
                }
            )
        } catch (e: EOFException) {
            // 该异常是因为当前插件没有设置，获取了空白导致 Gson 反序列化失败
            // 也认为获取插件设置信息成功，但是赋一个空的 PluginSettingForms 对象
            // 表示当前插件没有设置选项
            result.success(PluginSetting(PluginSettingForms()))
        } catch (e: Exception) {
            result.failure(e.message.toString())
            e.printStackTrace()
        }
        return result
    }
}