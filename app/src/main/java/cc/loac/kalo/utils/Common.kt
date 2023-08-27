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
    if (!this.contains("https://") && !this.contains("http://")) {
        return false
    }
    return this.matches(Regex(urlPattern))
}

/**
 * 使用公钥对文本进行 RSA 加密
 * @param str 要加密的文本
 * @param key 公钥
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

