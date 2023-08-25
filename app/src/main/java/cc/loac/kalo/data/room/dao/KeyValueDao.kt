package cc.loac.kalo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cc.loac.kalo.data.room.entity.KeyValue

/**
 * 键值对表接口
 */
@Dao
interface KeyValueDao {
    @Query("SELECT * FROM keyValue WHERE k = :key")
    suspend fun get(key: String): KeyValue?

    @Query("SELECT * FROM keyValue")
    suspend fun getAll(): List<KeyValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(keyValue: KeyValue)
}