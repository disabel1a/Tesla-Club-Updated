package com.example.teslaclub

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.teslaclub.databinding.ActivityMainPageBinding
import com.example.teslaclub.slides.ViewPagerAdapter
import com.example.teslaclub.user.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private lateinit var userViewModel: UserViewModel
    private var userId: Int = -1 // ID пользователя
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Получаем данные о пользователе из Intent
        val username = intent.getStringExtra("username")  // Получаем имя пользователя
        userId = intent.getIntExtra("userId", -1)  // Получаем ID пользователя
        isAdmin = intent.getStringExtra("isAdmin").toBoolean()

        if (userId == -1) {
            Toast.makeText(this, "Authorisation error", Toast.LENGTH_SHORT).show()
            finish() // Закрываем активность, если нет ID пользователя
        }

        // Настроим адаптер ViewPager2
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = ViewPagerAdapter(this, userId, isAdmin)
        viewPager.adapter = adapter

        // Соединяем ViewPager2 и TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Avaliable cars" else { if(isAdmin) "Add car" else "My profile"}
        }.attach()
    }
}


