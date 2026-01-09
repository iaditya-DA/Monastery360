package com.example.monastery360.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ManuscriptEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ManuscriptDatabase : RoomDatabase() {

    abstract fun manuscriptDao(): ManuscriptDao

    companion object {
        @Volatile
        private var INSTANCE: ManuscriptDatabase? = null

        fun getDatabase(context: Context): ManuscriptDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ManuscriptDatabase::class.java,
                    "manuscript_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}