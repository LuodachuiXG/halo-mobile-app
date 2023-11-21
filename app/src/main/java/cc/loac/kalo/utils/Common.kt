package cc.loac.kalo.utils


import android.util.Base64
import java.lang.StringBuilder
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
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
    return getRandomChat(8) + "-" +
            getRandomChat(4) + "-" +
            getRandomChat(4) + "-" +
            getRandomChat(4) + "-" +
            getRandomChat(12)
}


/**
 * 判断一段文本是否是网址
 */
fun String.isUrl(): Boolean {
    val urlPattern = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$"
    return this.matches(Regex(urlPattern))
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

