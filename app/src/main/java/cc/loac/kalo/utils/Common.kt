package cc.loac.kalo.utils

import java.lang.StringBuilder
import kotlin.random.Random


/**
 * 获取指定长度随机字符
 * @param length 字符长度
 * @return String
 */
fun getRandomChat(length: Int): String {
    val str = "abcdefghijklmnopqrstuvwxyz0123456789"
    var result = StringBuilder()
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
