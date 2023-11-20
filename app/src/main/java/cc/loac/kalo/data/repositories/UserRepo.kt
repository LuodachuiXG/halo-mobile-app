package cc.loac.kalo.data.repositories

import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.UserInfo
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.UserApiService
import cc.loac.kalo.network.handle

/**
 * 用户相关数据操作类
 * @param url Halo 站点地址
 */
class UserRepo(url: String) {
    private val url: String

    private var userApi: UserApiService

    init {
        userApi = RetrofitClient.create(url).userApiService
        this.url = url
    }

    /**
     * 获取用户个人资料
     */
    suspend fun getUserProfile(): MyResponse<UserInfo> {
        val result = MyResponse<UserInfo>()
        try {
            userApi.getUserProfile().handle(
                success = { userInfo, _ ->
                    var avatar = userInfo.user.spec.avatar
                    if (avatar.isNotEmpty() && !avatar.contains("http")) {
                        // 头像地址是相对头像，这里要加上 Halo 站点地址
                        avatar = ConfigRepo.get(ConfigKey.HALO_URL) + avatar
                    }
                    userInfo.user.spec.avatar = avatar
                    result.success(userInfo)
                },
                failure = {
                    result.failure(it.detail)
                }
            )
        } catch (e: Exception) {
            result.failure("错误")
            e.printStackTrace()
        }
        return result
    }
}