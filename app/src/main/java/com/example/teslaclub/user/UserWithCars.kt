package com.example.teslaclub.user

import androidx.room.Embedded
import androidx.room.Relation
import com.example.teslaclub.cars.Car

data class UserWithCars(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "ownerId"
    )
    val cars: List<Car>
)