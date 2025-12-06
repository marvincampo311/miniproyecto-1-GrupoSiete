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
     * GET TOTAL INVENTORY VALUE FLOW - Obtiene el total del inventario de manera reactiva
     *
     * Flow: Se actualiza automáticamente cuando cambian los productos
     * Uso: En el Widget para mostrar el saldo en tiempo real
     *
     * @return Flow<Double?> - Flujo del valor total (o null si no hay productos)
     */
    fun getTotalInventoryValueFlow(): Flow<Double?> {
        return productDao.getTotalInventoryValueFlow()
    }

    // ==================== OPERACIONES SUSPEND (SINGLE SHOT) ====================

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

    /**
     * GET PRODUCT BY ID - Obtiene un producto específico por su ID
     *
     * suspend: Operación asincrónica
     * @param productId: El ID del producto a buscar
     * @return Product?: El producto encontrado, o null si no existe
     */
    suspend fun getProductById(productId: Int): Product? {
        return productDao.getProductById(productId)
    }

    /**
     * UPDATE PRODUCT - Actualiza un producto existente
     *
     * suspend: Debe ser llamado desde una Coroutine
     * IMPORTANTE: El producto debe tener el mismo ID del que será actualizado
     * @param product: El producto con datos actualizados
     */
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    /**
     * DELETE PRODUCT - Elimina un producto de la BD
     *
     * suspend: Operación asincrónica
     * @param product: El producto a eliminar
     */
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    /**
     * DELETE PRODUCT BY ID - Elimina un producto usando solo su ID
     *
     * suspend: Operación asincrónica
     * @param productId: El ID del producto a eliminar
     * @return Int: Número de filas eliminadas (1 si fue exitoso, 0 si no encontró)
     */
    suspend fun deleteProductById(productId: Int): Int {
        return productDao.deleteProductById(productId)
    }

    /**
     * GET TOTAL INVENTORY VALUE - Calcula el valor total del inventario
     *
     * suspend: Operación asincrónica
     * Fórmula: SUM(price × cantidad) para todos los productos
     * @return Double?: Total del inventario, o null si no hay productos
     */
    suspend fun getTotalInventoryValue(): Double? {
        return productDao.getTotalInventoryValue()
    }
}