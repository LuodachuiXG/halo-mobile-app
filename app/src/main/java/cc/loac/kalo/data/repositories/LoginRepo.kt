package cc.loac.kalo.data.repositories

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PublicKey
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.LoginApiService
import cc.loac.kalo.network.handle
import cc.loac.kalo.utils.encryptData
import cc.loac.kalo.utils.generateToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 登录相关的数据操作类
 */
class LoginRepo(url: String) {

    // 站点地址
    private val url: String

    // LoginApi
    private var loginApi: LoginApiService
    init {
        // 获取 ApiService
        loginApi = RetrofitClient.create(url).loginApiService
        this.url = url
    }

    /**
     * 获取 Public-Key
     */
    private suspend fun getPublicKey(): MyResponse<PublicKey> {
        val result = MyResponse<PublicKey>()
        return try {
            val response = loginApi.getPublicKey()
            if (response.isSuccessful) {
                result.success(response)
            } else {
                result.failure(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result.failure(e.message.toString())
        }
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     */
    suspend fun login(
        username: String,
        password: String
    ): MyResponse<String> {
        // 返回的请求结果
        val result = MyResponse<String>()

        // 首先获取公钥
        val publicKeyResponse = getPublicKey()

        if (!publicKeyResponse.isSuccessful()) {
            // 获取公钥失败
            result.failure(publicKeyResponse.errMsg)
            return result
        }

        // 从请求响应中获取公钥
        val publicKey = publicKeyResponse.data ?:
            return result.apply {
                // 获取公钥为空
                failure("获取公钥为空，登录失败")
            }
        val key = publicKey.base64Format

        // 用公钥加密密码
        val encryptedPassword = encryptData(password, key)

        // 生成 CSRF Token
        val token = generateToken()
        Log.e(javaClass.name, encryptedPassword)
        try {
            // 尝试登录
            loginApi.login(token, username, encryptedPassword).handle(
                success = {
                    // 登录成功
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        ConfigRepo.apply {
                            // 登录信息保存到数据库
                            setToRoom(ConfigKey.HALO_URL, url)
                            setToRoom(ConfigKey.USERNAME, username)
                            setToRoom(ConfigKey.PASSWORD, password)

                            // 本次登录的 Token 保存到缓存中
                            set(ConfigKey.CSRF_TOKEN, token)
                        }
                    }
                    result.success()
                },
                failure = {
                    // 登录失败，回调网络请求异常信息（账号密码错误等）
                    result.failure(it.detail)
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            result.failure(e.message.toString())
        }
        return result
    }

}