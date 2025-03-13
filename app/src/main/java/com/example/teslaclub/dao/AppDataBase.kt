package com.example.teslaclub.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.teslaclub.cars.Car
import com.example.teslaclub.user.User

@Database(entities = [Car::class, User::class], version = 3)
abstract class AppDataBase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun userDao(): UserDao
}
