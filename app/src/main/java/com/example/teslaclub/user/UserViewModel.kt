package com.example.teslaclub.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.teslaclub.App
import com.example.teslaclub.cars.Car
import com.example.teslaclub.dao.AppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {
    // Получаем доступ к DAO через экземпляр базы данных в App
    private val userDao = (application as App).database.userDao()
    private val carDao = (application as App).database.carDao()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    // Получение всех пользователей
    fun getAllUsers(): LiveData<List<User>> = userDao.getAll()

    // Получение пароля пользователя
    suspend fun getPassword(username: String): String? {
        return userDao.getPassword(username)
    }

    // Регистрация пользователя
    fun registerUser(name: String, username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingPassword = userDao.getPassword(username)
            if (existingPassword == null) {
                val newUser = User(username = username, name = name, password = password, role = false)
                userDao.insert(newUser)
                withContext(Dispatchers.Main) { callback(true) }
            } else {
                withContext(Dispatchers.Main) { callback(false) }
            }
        }
    }

    // Получение машин пользователя
    fun getCarsByUser(userId: Int): LiveData<List<Car>> = carDao.getCarsByOwner(userId)

    // Загрузка данных пользователя
    fun getUserById(userId: Int): LiveData<User?> {
        return userDao.getUserById(userId)
    }

    // Добавьте suspend функцию для получения id пользователя
    suspend fun getUserId(username: String): Int? {
        return userDao.getUserByUsername(username)?.id
    }

    suspend fun isAdmin(userId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.getAdminStatus(userId)
        }
    }
}
