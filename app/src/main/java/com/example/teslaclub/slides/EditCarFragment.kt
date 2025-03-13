package com.example.teslaclub.slides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.teslaclub.App
import com.example.teslaclub.R
import com.example.teslaclub.cars.Car
import com.example.teslaclub.dao.CarDao
import com.example.teslaclub.databinding.AddCarActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCarFragment : Fragment() {

    private lateinit var binding: AddCarActivityBinding
    private lateinit var carDao: CarDao
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AddCarActivityBinding.inflate(inflater, container, false)
        carDao = (requireActivity().application as App).database.carDao()

        // Настраиваем список моделей
        val models = listOf("Tesla Model S", "Tesla Model 3", "Tesla Model X", "Tesla Model Y", "Tesla Cybertruck")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, models)
        binding.modelSpinner.adapter = adapter

        // Устанавливаем значения полей для редактирования
        val modelPos = models.indexOf(arguments?.getString("carModel"))
        binding.modelSpinner.setSelection(modelPos)

        binding.vinEdit.setText(arguments?.getString("carVin"))
        binding.yearEdit.setText(arguments?.getString("carYear"))
        binding.priceEdit.setText(arguments?.getString("carPrice"))

        // Получаем ViewPager2, чтобы переключить фрагменты
        viewPager = requireActivity().findViewById(R.id.viewPager)

        binding.submitButton.setOnClickListener {
            val id = arguments?.getString("carId")!!.toInt()

            val model = binding.modelSpinner.selectedItem.toString()
            val vin = binding.vinEdit.text.toString()
            val year = binding.yearEdit.text.toString().toInt()
            val price = binding.priceEdit.text.toString().toInt()

            val car = Car(id = id, model = model, vin = vin, year = year, price = price, ownerId = null)

            // Обновляем данные о машине в базе данных
            lifecycleScope.launch(Dispatchers.IO) {
                carDao.update(car)
            }

            // Переключаемся на фрагмент доступных автомобилей
            viewPager.currentItem = 0 // Индекс фрагмента AvailableCarsFragment
        }

        return binding.root
    }

    companion object {
        fun newInstance(carId: Int, carModel: String, carVin: String, carYear: String, carPrice: String): EditCarFragment {
            val fragment = EditCarFragment()
            val bundle = Bundle()
            bundle.putInt("carId", carId)
            bundle.putString("carModel", carModel)
            bundle.putString("carVin", carVin)
            bundle.putString("carYear", carYear)
            bundle.putString("carPrice", carPrice)
            fragment.arguments = bundle
            return fragment
        }
    }
}
