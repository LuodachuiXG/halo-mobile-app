package cc.loac.kalo.network


import android.annotation.SuppressLint
import cc.loac.kalo.data.repositories.ConfigKey
import cc.loac.kalo.data.repositories.ConfigRepo
import cc.loac.kalo.network.api.LoginApiService
import cc.loac.kalo.network.api.PluginApiService
import cc.loac.kalo.network.api.UserApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


/**
 * Retrofit 网络请求类
 */
class RetrofitClient private constructor(retrofit: Retrofit) {
    companion object {
        /**
         * 获取 RetrofitClient 实例
         * @param url Halo 站点地址
         */
        fun create(url: String = ConfigRepo.get(ConfigKey.HALO_URL)): RetrofitClient {
            val httpClient = OkHttpClient.Builder()
            httpClient.apply {
                // 添加拦截器，用于给每个请求头加上登录验证的 Session
                addInterceptor {
                    val originalRequest = it.request()
                    val request = originalRequest.newBuilder()
                        .header("Cookie", "SESSION=${ConfigRepo.get(ConfigKey.SESSION_TOKEN)}")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .method(originalRequest.method, originalRequest.body)
                        .build()
                    it.proceed(request)
                }
                // 忽略对 https 证书的验证
                sslSocketFactory(SSLSocketClient.sSLSocketFactory, SSLSocketClient.trustManager)
                hostnameVerifier(SSLSocketClient.hostnameVerifier)
                build()
            }

            // 设置 Gson 序列化的时间格式
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'").create()
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
            return RetrofitClient(retrofit)
        }
    }

    /**
     * 登录 Api
     */
    val loginApiService: LoginApiService = retrofit.create(LoginApiService::class.java)

    /**
     * 用户 Api
     */
    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)

    /**
     * 插件 Api
     */
    val pluginApiService: PluginApiService = retrofit.create(PluginApiService::class.java)
}

/**
 * 忽略证书验证
 */
private object SSLSocketClient {
    val sSLSocketFactory: SSLSocketFactory
        // 获取 SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustManager), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    val trustManager = @SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }
    }
    val hostnameVerifier: HostnameVerifier
        //获取HostnameVerifier
        get() {
            return HostnameVerifier { _, _ -> true }
        }
}