package com.example.miiproyecto1

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val codigo: String,
    val name: String,
    val price: Double,
    val cantidad: Int


)