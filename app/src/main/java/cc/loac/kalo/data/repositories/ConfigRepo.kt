package cc.loac.kalo.data.repositories


import cc.loac.kalo.MainActivity
import cc.loac.kalo.data.room.AppDataBase
import cc.loac.kalo.data.room.dao.KeyValueDao
import cc.loac.kalo.data.room.entity.KeyValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * 程序配置类
 * 在配置类初始化时从数据库中读取所有配置缓存起来，
 * 所以在程序运行时，所有的获取配置都是从缓存中获取，
 * 添加或修改配置时写到数据库并同时修改缓存中的数据
 */
object ConfigRepo  {
    /**
     * KeyValue 接口
     */
    private var dao: KeyValueDao = AppDataBase.getInstance(MainActivity.appContext).keyValueDao()

    /**
     * 将配置表中的配置信息缓存
     */
    private val caches = HashMap<String, String>()

    init {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            // 在类初始化时从数据库中读取所有数据存到缓存中
            val list = dao.getAll()
            list.forEach {
                caches[it.k] = it.v
            }
        }
    }

    /**
     * 将数据缓存到集合中
     */
    private fun cache(key: ConfigKey, value: String) {
        if (caches.containsKey(key.key)) {
            caches.replace(key.key, value)
        } else {
            caches[key.key] = value
        }
    }

    /**
     * 从缓存中获取配置
     * @return 不存在返回空文本
     */
    fun get(key: ConfigKey): String {
        return caches[key.key] ?: ""
    }

    /**
     * 从 Room 数据库中获取配置
     * @param key 键
     */
    suspend fun getByRoom(key: ConfigKey): String {
        val keyValue = dao.get(key.key)?.v ?: ""
        // 从数据库获取数据的同时也写入缓存
        cache(key, keyValue)
        return keyValue
    }

    /**
     * 修改配置
     * @param key 键
     * @param value 值
     */
    suspend fun set(key: ConfigKey, value: String) {
        dao.set(KeyValue(key.key, value))
        // 同时将配置保存到缓存中
        cache(key, value)
    }
}

/**
 * 配置信息枚举类
 */
enum class ConfigKey(val key: String) {
    // Halo 站点地址
    HALO_URL("halo_url")
}