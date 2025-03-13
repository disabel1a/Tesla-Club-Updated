package com.example.teslaclub

import android.app.Application
import androidx.room.Room
import com.example.teslaclub.dao.AppDataBase

class App : Application() {
    lateinit var database: AppDataBase

    override fun onCreate() {
        super.onCreate()
        // Создаем базу данных с использованием одного имени
        database = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "app_database")
            .fallbackToDestructiveMigration() // Очищение базы данных при изменении схемы
            .build()
    }
}
