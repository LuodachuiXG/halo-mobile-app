package cc.loac.kalo.data.repositories

import android.util.Log
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PublicKey
import cc.loac.kalo.network.RetrofitClient
import cc.loac.kalo.network.api.LoginApiService
import cc.loac.kalo.network.handle
import cc.loac.kalo.utils.encryptData
import cc.loac.kalo.utils.generateToken
import cc.loac.kalo.utils.sub
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
                // 获取公钥失败
                result.failure(
                    "获取登录加密公钥失败，请检查网络以及站点地址是否正确。\n" +
                            "并检查 Halo 版本是否兼容，当前 App 不支持 Halo 2.0 以下版本，" +
                            "如果你使用的是 Halo 1.* 版本，请下载 v0.0.5 版本。"
                )
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
        val scope = CoroutineScope(Dispatchers.IO)
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
        try {
            // 尝试登录
            loginApi.login(token, username, encryptedPassword).handle(
                success = { _, response ->
                    // 登录成功
                    // 获取登录成功的 Session Cookie
                    val sessionCookie = response.headers()["set-cookie"]
                    // sessionCookie 为空，登录失败
                    if (sessionCookie == null) {
                        result.failure("登录 Session 获取失败，登陆失败")
                        return@handle
                    }

                    // 从 Cookie 中截取出 Session
                    val session = sessionCookie.sub("SESSION=", ";")
                    if (session.isEmpty()) {
                        result.failure("登录 Session 获取失败，登陆失败")
                        return@handle
                    }
                    scope.launch {
                        ConfigRepo.apply {
                            // 登录信息保存到数据库
                            setToRoom(ConfigKey.HALO_URL, url)
                            setToRoom(ConfigKey.USERNAME, username)
                            setToRoom(ConfigKey.PASSWORD, password)

                            // 本次登录的 Session Token 保存到缓存中
                            set(ConfigKey.SESSION_TOKEN, session)
                        }
                    }
                    result.success()
                }
            ) {
                // 登录失败，回调网络请求异常信息（账号密码错误等）
                result.failure(it.detail)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result.failure(e.message.toString())
        }
        return result
    }

}