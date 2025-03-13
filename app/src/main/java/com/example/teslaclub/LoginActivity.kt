package com.example.teslaclub.user

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.teslaclub.MainPageActivity
import com.example.teslaclub.R
import com.example.teslaclub.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel
    private var isSignUpMode = false // Флаг для отслеживания режима регистрации
    private var defaultBackground: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.loginButton.setOnClickListener {loginUser()}

        binding.signUpButton.setOnClickListener {
            if (isSignUpMode) {
                // Если мы уже в режиме регистрации, выполняем регистрацию
                registerUser()
            } else {
                // Переключаемся в режим регистрации
                switchToSignUpMode()
            }
        }

        defaultBackground = binding.signUpButton.background
    }

    private fun switchToSignUpMode() {
        isSignUpMode = true

        // Делаем поле Name видимым
        binding.name.visibility = android.view.View.VISIBLE

        // Изменяем topMargin для поля username
        val layoutParams = binding.username.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        layoutParams.topMargin = 8
        binding.username.layoutParams = layoutParams

        // Меняем кнопку Login на SignUp
        binding.loginButton.visibility = android.view.View.GONE
        binding.signUpButton.background = resources.getDrawable(R.drawable.login_btn_bg)

        val sbLayoutParams = binding.signUpButton.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        sbLayoutParams.width = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        sbLayoutParams.topMargin = 8
        binding.signUpButton.layoutParams = sbLayoutParams
    }

    private fun resetToLoginMode() {
        isSignUpMode = false

        // Скрываем поле Name
        binding.name.visibility = android.view.View.GONE

        // Восстанавливаем topMargin для поля username
        val layoutParams = binding.username.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        layoutParams.topMargin = 16
        binding.username.layoutParams = layoutParams

        // Меняем кнопку SignUp на Login
        binding.loginButton.visibility = android.view.View.VISIBLE
        binding.signUpButton.background = defaultBackground

        val sbLayoutParams = binding.signUpButton.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        sbLayoutParams.width =  androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
        sbLayoutParams.topMargin = 0
        binding.signUpButton.layoutParams = sbLayoutParams
    }

    private fun registerUser() {
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val name = binding.name.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingPassword = userViewModel.getPassword(username)
            if (existingPassword == null) {
                val hashedPassword = password // Можно использовать хэширование для безопасности
                userViewModel.registerUser(name, username, hashedPassword) { success ->
                    if (success) {
                        Toast.makeText(this@LoginActivity, "User registered", Toast.LENGTH_SHORT).show()
                        resetToLoginMode() // Возвращаемся в режим логина
                    } else {
                        Toast.makeText(this@LoginActivity, "Login is busy", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Login is busy", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser() {
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter Login and Password", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val storedPassword = userViewModel.getPassword(username)
            if (storedPassword != null && storedPassword == password) {
                Toast.makeText(this@LoginActivity, "Successful login", Toast.LENGTH_SHORT).show()

                // Получаем информацию о пользователе (например, userId) из модели или базы данных
                val userId = userViewModel.getUserId(username) // Предполагаем, что у вас есть метод для получения ID
                var isAdmin = true
                if (userId != null) {
                    isAdmin = userViewModel.isAdmin(userId)
                }

                // Передаем username и userId в MainPageActivity
                val intent = Intent(this@LoginActivity, MainPageActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("userId", userId)  // Передаем userId
                intent.putExtra("isAdmin", isAdmin.toString())

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Wrong Login or Password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
