package cc.loac.kalo.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cc.loac.kalo.data.room.entity.KeyValue
import cc.loac.kalo.data.room.dao.KeyValueDao


/**
 * Room 数据库抽象类
 */
@Database(entities = [KeyValue::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase {
            // 每次只能有一个执行线程进入该代码块
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "myDatabase"
                        ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    /**
     * 键值对接口 Dao
     */
    abstract fun keyValueDao(): KeyValueDao
}