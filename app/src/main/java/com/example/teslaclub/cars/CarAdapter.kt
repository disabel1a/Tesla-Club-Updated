import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.teslaclub.R
import com.example.teslaclub.cars.Car
import com.example.teslaclub.dao.CarDao
import com.example.teslaclub.slides.EditCarFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CarAdapter(
    private val carDao: CarDao,
    private val activity: FragmentActivity,
    private val userId : Int,
    private val isAdmin: Boolean,
    private val isUserPage: Boolean
) : RecyclerView.Adapter<CarAdapter.ViewHolder>() {

    private var cars: List<Car> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cars[position])
    }

    override fun getItemCount(): Int = cars.size

    fun setCarsList(cars: List<Car>) {
        this.cars = cars
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val image: ImageView = itemView.findViewById(R.id.imageView)
        private val model: TextView = itemView.findViewById(R.id.model)
        private val vin: TextView = itemView.findViewById(R.id.vin)
        private val year: TextView = itemView.findViewById(R.id.year)
        private val price: TextView = itemView.findViewById(R.id.price)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener {
                // Получаем ID текущего пользователя и передаем в showPopupMenu
                showPopupMenu(it, adapterPosition, userId, isUserPage)
                true
            }
        }

        fun bind(car: Car) {
            model.text = car.model
            vin.text = car.vin
            year.text = car.year.toString()
            price.text = car.price.toString() + "$"

            val carImageResourceId = getCarImageResource(car.model)
            if (carImageResourceId != null) {
                image.setImageResource(carImageResourceId)
            }
        }

        override fun onClick(v: View?) {
            // Handle item clicks here if needed
        }

        private fun getCarImageResource(model: String): Int? {
            val carModelImageMap = mapOf(
                "Tesla Model 3" to R.drawable.tesla_model_3,
                "Tesla Model X" to R.drawable.tesla_model_x,
                "Tesla Model Y" to R.drawable.tesla_model_y,
                "Tesla Model S" to R.drawable.tesla_model_s,
                "Tesla Cybertruck" to R.drawable.tesla_cybertruck
            )

            return carModelImageMap[model]
        }
    }

    private fun showPopupMenu(view: View, position: Int, currentUserId: Int, isUserPage: Boolean) {
        val popupMenu = PopupMenu(view.context, view)

        // Если пользователь администратор, добавляем "Remove" и "Edit"
        if (isAdmin) {
            popupMenu.menu.add(0, 1, Menu.NONE, "Edit")
            popupMenu.menu.add(0, 2, Menu.NONE, "Remove")
        } else {
            // Если пользователь не администратор, только "Rent"
            if (isUserPage) {
                popupMenu.menu.add(0, 3, Menu.NONE, "Return ")
            }
            else {
                popupMenu.menu.add(0, 3, Menu.NONE, "Rent")
            }
        }

        // Обработчик выбора пункта меню
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> { // Редактирование
                    val car = cars[position]
                    val fragment = EditCarFragment.newInstance(
                        car.id, car.model, car.vin, car.year.toString(), car.price.toString()
                    )

                    // Переключаемся на фрагмент редактирования
                    val containerId = R.id.fragment_container // Убедитесь, что это правильный ID контейнера
                    activity.supportFragmentManager.beginTransaction()
                        .replace(containerId, fragment) // Используем правильный контейнер
                        .addToBackStack(null)
                        .commit()

                    true
                }
                2 -> { // Удаление (только для администратора)
                    CoroutineScope(Dispatchers.IO).launch {
                        val car = cars[position]
                        carDao.delete(car)
                    }
                    notifyItemRemoved(position)
                    true
                }
                3 -> { // Аренда (только для обычного пользователя)
                    val car = cars[position]

                    // Логика аренды: обновляем владельца в базе данных
                    CoroutineScope(Dispatchers.IO).launch {
                        if (isUserPage)
                        {
                            carDao.updateOwner(car.id, null) // Метод для обновления ownerId
                        }
                        else {
                            carDao.updateOwner(car.id, currentUserId) // Метод для обновления ownerId
                        }
                    }
                    notifyItemChanged(position)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}
