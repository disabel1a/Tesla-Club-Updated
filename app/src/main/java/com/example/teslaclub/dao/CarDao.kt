package com.example.teslaclub.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.teslaclub.cars.Car

@Dao
interface CarDao {
    @Insert
    suspend fun insert(car: Car): Long

    @Update
    suspend fun update(car: Car): Int

    @Delete
    suspend fun delete(car: Car): Int

    @Query("SELECT * FROM car")
    fun getAll(): LiveData<List<Car>>

    @Query("SELECT * FROM car WHERE ownerId = :ownerId")
    fun getCarsByOwner(ownerId: Int): LiveData<List<Car>>

    @Query("SELECT * FROM car WHERE ownerId IS NULL")
    fun getAllAvailableCars(): LiveData<List<Car>>

    @Query("UPDATE car SET ownerId = :ownerId WHERE id = :carId")
    suspend fun updateOwner(carId: Int, ownerId: Int?)
}