package com.example.teslaclub.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.teslaclub.user.User
import com.example.teslaclub.user.UserWithCars

@Dao
interface UserDao {
    @Insert
    fun insert(user: User): Long

    @Update
    fun update(user: User): Int

    @Delete
    fun delete(user: User): Int

    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT password FROM user WHERE username = :username")
    suspend fun getPassword(username: String): String

    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserWithCars(userId: Int): LiveData<UserWithCars>

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User?>

    @Query("SELECT role FROM user WHERE id = :userId")
    fun getAdminStatus(userId: Int): Boolean
}
