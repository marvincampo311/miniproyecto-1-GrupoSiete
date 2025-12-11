package com.example.miiproyecto1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,              // ID local de Room

    val remoteId: String? = null, // ID de documento en Firestore

    val codigo: String,
    val name: String,
    val price: Double,
    val cantidad: Int
)
