package com.example.monastery360.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.monastery360.database.PostDao
import com.example.monastery360.database.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class MonasteryDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}