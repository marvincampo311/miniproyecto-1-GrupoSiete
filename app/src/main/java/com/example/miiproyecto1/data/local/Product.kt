package com.example.miiproyecto1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTIDAD PRODUCT - MAPEO A LA TABLA DE BASE DE DATOS
 *
 * @Entity: Indica que esta clase se mapea a una tabla en Room
 * tableName: Especifica el nombre de la tabla en la base de datos
 *
 * PROPIEDADES:
 * - id: Identificador único (autogenerado)
 * - codigo: Código único del producto (máx 4 dígitos)
 * - name: Nombre del producto (máx 40 caracteres)
 * - price: Precio unitario del producto
 * - cantidad: Cantidad disponible en inventario
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val codigo: String,      // Ej: "1001"
    val name: String,        // Ej: "Laptop"
    val price: Double,       // Ej: 1200.50
    val cantidad: Int        // Ej: 5
)