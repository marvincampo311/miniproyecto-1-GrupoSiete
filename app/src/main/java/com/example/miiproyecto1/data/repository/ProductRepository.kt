package com.example.miiproyecto1.data.repository

import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.local.ProductDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * PRODUCT REPOSITORY - PATRÓN REPOSITORY (CLEAN ARCHITECTURE)
 *
 * RESPONSABILIDADES:
 * 1. Única clase que accede al ProductDao
 * 2. Todas las funciones son suspend para ser llamadas desde Coroutines
 * 3. Expone datos reactivos mediante Flow
 * 4. Maneja la lógica de acceso a datos
 *
 * VENTAJAS:
 * - Centraliza todas las operaciones de BD
 * - Los ViewModels no conocen los detalles de Room
 * - Fácil de testear (mockeamos el Dao)
 * - Escalable para agregar múltiples fuentes de datos
 *
 * @Inject: Permite que Hilt inyecte el ProductDao automáticamente
 */
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    // ==================== OPERACIONES REACTIVAS (FLOW) ====================

    /**
     * GET ALL PRODUCTS FLOW - Obtiene todos los productos de manera reactiva
     *
     * Flow: Emite automáticamente nuevos valores cuando la BD cambia
     * Ventaja: El HomeFragment se actualiza automáticamente sin refrescar manualmente
     *
     * @return Flow<List<Product>> - Flujo de lista de productos
     */
    fun getAllProductsFlow(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }

    /**
     * INSERT PRODUCT - Inserta un nuevo producto en la BD
     *
     * suspend: Debe ser llamado desde una Coroutine (viewModelScope.launch)
     * @param product: El producto a insertar
     * @return Long: El ID generado por Room para el producto
     */
    suspend fun insertProduct(product: Product): Long {
        return productDao.insertProduct(product)
    }


    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

}