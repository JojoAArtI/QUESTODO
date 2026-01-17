package com.example.quest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quest.data.local.dao.TaskDao
import com.example.quest.data.local.dao.CategoryDao
import com.example.quest.data.local.entity.TaskEntity
import com.example.quest.data.local.entity.CategoryEntity

@Database(
    entities = [
        TaskEntity::class,
        CategoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TaskQuestDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TaskQuestDatabase? = null

        fun getDatabase(context: Context): TaskQuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskQuestDatabase::class.java,
                    "taskquest_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
