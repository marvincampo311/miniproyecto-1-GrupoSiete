package com.example.miiproyecto1.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

/**
 * DATA ACCESS OBJECT (DAO) - INTERFAZ PARA ACCESO A BASE DE DATOS
 *
 * IMPORTANTE: Todas las funciones son 'suspend' para ser llamadas desde
 * Coroutines sin bloquear el hilo principal.
 *
 * El Repositorio será la ÚNICA clase que acceda a estos métodos.
 */
@Dao
interface ProductDao {

    /**
     * INSERTAR un producto en la base de datos de manera asincrónica
     *
     * suspend: Puede pausarse y ejecutarse en un hilo separado (IO)
     * return: Long con el ID generado del producto
     */
    @Insert
    suspend fun insertProduct(product: Product): Long

    /**
     * OBTENER TODOS los productos como un Flow (reactividad)
     *
     * Flow: Emite valores cada vez que los datos cambian
     * Ordenados por ID descendente (productos más recientes primero)
     */
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    /**
     * OBTENER UN producto por su ID
     *
     * suspend: Operación asincrónica
     * return: El producto encontrado o null si no existe
     */
    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): Product?

    /**
     * ELIMINAR UN producto completamente
     *
     * suspend: Operación asincrónica
     */
    @Delete
    suspend fun deleteProduct(product: Product)

    /**
     * ACTUALIZAR un producto existente
     *
     * suspend: Operación asincrónica
     * IMPORTANTE: El producto debe tener el mismo ID del que se va a actualizar
     */
    @Update
    suspend fun updateProduct(product: Product)

    /**
     * ELIMINAR un producto por su ID
     *
     * suspend: Operación asincrónica
     * return: Cantidad de filas eliminadas (1 si fue exitoso)
     */
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int): Int

    /**
     * CALCULAR EL VALOR TOTAL del inventario
     *
     * Suma: (precio × cantidad) de TODOS los productos
     * return: Double con el total, o null si no hay productos
     *
     * NOTA: Esta función se usa en el Widget para mostrar el saldo
     */
    @Query("SELECT SUM(price * cantidad) FROM products")
    suspend fun getTotalInventoryValue(): Double?

    /**
     * OBTENER EL VALOR TOTAL como Flow (para reactividad)
     *
     * Útil para actualizar el Widget en tiempo real
     */
    @Query("SELECT SUM(price * cantidad) FROM products")
    fun getTotalInventoryValueFlow(): Flow<Double?>


    /**
     * GET ALL PRODUCTS SYNC - Obtiene productos de forma sincrónica
     *
     * Usado por el Widget (que necesita acceso sincrónico)
     * NO usa Flow/Coroutines, retorna List directa
     */
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProductsSync(): List<Product>

    // para sincronizar con firestoreee
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProducts(products: List<Product>)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()



}