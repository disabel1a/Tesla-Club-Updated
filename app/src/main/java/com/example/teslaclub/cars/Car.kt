package com.example.teslaclub.cars

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.teslaclub.user.User

@Entity(
    tableName = "car",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.SET_NULL // При удалении пользователя ownerId станет NULL
        )
    ],
    indices = [Index(value = ["ownerId"])] // Оптимизация поиска по ownerId
)
data class Car(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var model: String,
    var vin: String,
    var year: Int,
    var price: Int,
    var ownerId: Int? // Может быть NULL, если владельца нет
)
