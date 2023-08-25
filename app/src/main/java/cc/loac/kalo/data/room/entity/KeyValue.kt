package cc.loac.kalo.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 数据库
 * 键值对表
 */
@Entity(tableName = "keyValue")
data class KeyValue(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "k")
    var k: String,
    @ColumnInfo(name = "v")
    var v: String
)