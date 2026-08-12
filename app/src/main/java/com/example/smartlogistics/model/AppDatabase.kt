package com.example.smartlogistics.model

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [LecturaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lecturaDao(): LecturaDao
}
