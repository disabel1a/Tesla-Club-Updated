package com.example.teslaclub.slides

import CarAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teslaclub.App
import com.example.teslaclub.R
import com.example.teslaclub.dao.CarDao
import com.example.teslaclub.databinding.UserViewActivityBinding
import com.example.teslaclub.user.UserViewModel

class UserProfileFragment : Fragment() {

    private lateinit var binding: UserViewActivityBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: CarAdapter
    private lateinit var carDao: CarDao
    private var isAdmin: Boolean = false
    private var currentUserId: Int = 0

    companion object {
        private const val ARG_USER_ID = "userId"
        private const val ARG_IS_ADMIN = "isAdmin"

        fun newInstance(userId: Int, isAdmin: Boolean): UserProfileFragment {
            val fragment = UserProfileFragment()
            val args = Bundle()
            args.putInt(ARG_USER_ID, userId)
            args.putBoolean(ARG_IS_ADMIN, isAdmin)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUserId = it.getInt(ARG_USER_ID, 0)
            isAdmin = it.getBoolean(ARG_IS_ADMIN, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = UserViewActivityBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        carDao = (requireActivity().application as App).database.carDao()

        // Получаем данные текущего пользователя
        val sharedPrefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userId = arguments?.getInt(ARG_USER_ID, -1) ?: -1
        isAdmin = arguments?.getBoolean(ARG_IS_ADMIN, false) ?: false

        if (userId == -1) {
            Toast.makeText(requireContext(), "Load profile error", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        // Загружаем информацию о пользователе
        userViewModel.getUserById(userId).observe(viewLifecycleOwner) { user ->
            binding.usernameView.text = user?.username ?: "Unknown"
            binding.nameView.text = user?.name ?: "Noname"
        }

        // Настраиваем RecyclerView для списка автомобилей пользователя
        adapter = CarAdapter(carDao, requireActivity(), currentUserId, isAdmin, isUserPage = true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Загружаем автомобили пользователя
        carDao.getCarsByOwner(userId).observe(viewLifecycleOwner) { cars ->
            adapter.setCarsList(cars)
        }

        return binding.root
    }
}
