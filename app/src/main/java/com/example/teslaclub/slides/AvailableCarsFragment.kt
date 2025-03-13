package com.example.teslaclub.slides

import CarAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.teslaclub.App
import com.example.teslaclub.R
import com.example.teslaclub.dao.CarDao
import com.example.teslaclub.databinding.RecyclerViewActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AvailableCarsFragment : Fragment() {

    private lateinit var binding: RecyclerViewActivityBinding
    private lateinit var adapter: CarAdapter
    private lateinit var carDao: CarDao
    private var isAdmin: Boolean = false
    private lateinit var viewPager: ViewPager2
    private var currentUserId: Int = 0

    companion object {
        private const val ARG_USER_ID = "userId"
        private const val ARG_IS_ADMIN = "isAdmin"

        fun newInstance(userId: Int, isAdmin: Boolean): AvailableCarsFragment {
            val fragment = AvailableCarsFragment()
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
        binding = RecyclerViewActivityBinding.inflate(inflater, container, false)
        carDao = (requireActivity().application as App).database.carDao()

        // Показываем кнопку "Добавить авто" только если пользователь — админ
        binding.addButton.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Настроим RecyclerView
        adapter = CarAdapter(carDao, requireActivity(), currentUserId, isAdmin, isUserPage = false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Загружаем список доступных авто
        loadAvailableCars()

        // Настройка ViewPager2
        viewPager = requireActivity().findViewById(R.id.viewPager)

        // Обработчик нажатия кнопки "Добавить авто" (только для админа)
        binding.addButton.setOnClickListener {
            viewPager.currentItem = 2  // Переключаемся на AddCarFragment
        }

        return binding.root
    }

    private fun loadAvailableCars() {
        carDao.getAllAvailableCars().observe(viewLifecycleOwner) { cars ->
            adapter.setCarsList(cars)
        }
    }
}
