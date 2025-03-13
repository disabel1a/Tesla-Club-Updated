package com.example.teslaclub.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class User (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var username: String,
    var name: String,
    var password: String,
    var role: Boolean
)