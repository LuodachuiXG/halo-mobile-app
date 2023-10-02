package cc.loac.kalo.data.repositories

import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.UserApiService

/**
 * 用户相关数据操作类
 * @param url Halo 站点地址
 */
class UserRepo(url: String) {
    private val url: String

    private var userApiService: UserApiService

    init {
        userApiService = RetrofitClient.create(url).userApiService
        this.url = url
    }

    /**
     * 获取用户个人资料
     */
    suspend fun getUserProfile(): MyResponse<String> {
        val result = MyResponse<String>()
        try {
            val userProfile = userApiService.getUserProfile(ConfigRepo.get(ConfigKey.CSRF_TOKEN))
            result.success(userProfile.string())
        } catch (e: Exception) {
            result.failure("错误")
            e.printStackTrace()
        }
        return result
    }

}