package com.example.miiproyecto1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface ProductDao {
    @Insert
    fun insertProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): List<Product>

    @Delete
    fun deleteProduct(product: Product)

    // Método para actualizar un producto existente
    @Update
    fun updateProduct(product: Product)

    // Alternativamente, eliminar por id (opcional)
    @Query("DELETE FROM products WHERE id = :productId")
    fun deleteProductById(productId: Int)

    //suma de precios de los productos (más eficiente)
    @Query("SELECT SUM(price) FROM products")
    fun getTotalInventoryValue(): Double?

}
