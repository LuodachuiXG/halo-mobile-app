package cc.loac.kalo.utils


import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import android.widget.Toast
import cc.loac.kalo.MainActivity
import com.google.gson.Gson
import java.lang.StringBuilder
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.crypto.Cipher
import kotlin.random.Random

/**
 * 获取指定长度随机字符
 * @param length 字符长度
 * @return String
 */
fun getRandomChat(length: Int): String {
    val str = "abcdefghijklmnopqrstuvwxyz0123456789"
    val result = StringBuilder()
    for (i in 0 until length) {
        result.append(str[Random.nextInt(0, 36)])
    }

    return result.toString()
}

/**
 * 生成 Token
 * @return String
 */
fun generateToken(): String {
    return UUID.randomUUID().toString()
}


/**
 * 判断一段文本是否是网址或者 IP 并格式化
 * @return 如果网址正确，返回格式化（如果需要）后的网址
 * 否则返回空文本
 */
fun String.formatURL(): String {
    val urlPattern = "^(https?://)([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$"
    val ipPortPattern = "^(https?://)?((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]" +
            "|2[0-4][0-9]|[01]?[0-9][0-9]?)((:(?:[1-9]\\d{0,3}|[1-5]\\d{4}|6[0-5][0-5][0-3][0-5]))?)$"
    if (this.matches(Regex(urlPattern))) {
        return this
    }

    // 当前 URL 是 IP 地址
    if (this.matches(Regex(ipPortPattern))) {
        return if (this.contains("http")) {
            this
        } else {
            "http://$this"
        }
    }

    return ""
}

/**
 * 获取 [startString] 和 [endString] 中间的字符串
 * @param startString 开始字符串，为空则代表从头开始
 * @param endString 结束字符串（从开始字符串后匹配到的第一个字符串后截止）
 *                  为空则代表到字符串最后
 * @return 返回中间字符串，匹配失败返回空文本
 */
fun String.sub(startString: String, endString: String): String {
    if (this.isEmpty()) {
        return ""
    }

    var startIndex = if (startString.isEmpty()) 0 else this.indexOf(startString)
    if (startIndex == -1) {
        // 开始字符串匹配失败
        return ""
    }

    // 把开始截取的索引放到开始字符之后
    startIndex += startString.length

    val endIndex = if (endString.isEmpty()) this.length else this.indexOf(endString, startIndex)
    if (endIndex == -1) {
        // 结束字符串匹配失败，直接从开始位置截取到文本最后
        return this.substring(startIndex, this.lastIndex)
    }

    return this.substring(startIndex, endIndex)
}

/**
 * 使用公钥对文本进行 RSA 加密
 * @param str 要加密的文本
 * @param key 公钥
 * @return 加密后的文本
 */
fun encryptData(str: String, key: String): String {
    // 将文本格式的公钥解码为 PublicKey 对象
    val keyBytes = Base64.decode(key, Base64.DEFAULT)
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    val keyPublic = keyFactory.generatePublic(keySpec)

    // 使用公钥对文本加密
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, keyPublic)
    val encryptedBytes = cipher.doFinal(str.toByteArray())
    // NO_WRAP 表示不在结果中加上换行和空格
    return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
}

/**
 * 字符串扩展，toast
 */
fun String.toast() {
    Toast.makeText(MainActivity.appContext, this, Toast.LENGTH_SHORT).show()
}

/**
 * 对 Date 扩展函数，格式化日期输出
 */
@SuppressLint("SimpleDateFormat")
fun Date.formatString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return sdf.format(this)
}


/**
 * 对对象进行序列化
 */
fun Any.toJson(): String {
    val gson = Gson()
    return gson.toJson(this)
}

