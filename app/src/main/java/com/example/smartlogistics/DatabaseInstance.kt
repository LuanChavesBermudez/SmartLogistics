package com.example.smartlogistics

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.example.smartlogistics.model.AppDatabase

class DatabaseInstance : Application() {
    val db: AppDatabase by lazy {
        Room.databaseBuilder<AppDatabase>(applicationContext, "lecturas-database")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
}