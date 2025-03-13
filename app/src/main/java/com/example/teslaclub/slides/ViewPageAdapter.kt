package com.example.teslaclub.slides

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity, private val userId: Int, private val isAdmin: Boolean)
    : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2 // Три экрана: список авто, профиль пользователя и добавление авто

    override fun createFragment(position: Int): Fragment {

        return if (isAdmin) {
            when (position) {
                0 -> AvailableCarsFragment.newInstance(userId, isAdmin) // Передаем userId и isAdmin
                else -> AddCarFragment()
            }
        } else {
            when (position) {
                0 -> AvailableCarsFragment.newInstance(userId, isAdmin) // Передаем userId и isAdmin
                else -> UserProfileFragment.newInstance(userId, isAdmin)
            }
        }
//        return when (position) {
//            0 -> AvailableCarsFragment.newInstance(userId, isAdmin) // Передаем userId и isAdmin
//            1 -> UserProfileFragment.newInstance(userId, isAdmin) // Экран профиля пользователя
//            else -> AddCarFragment() // Экран добавления нового авто
//        }
    }
}
