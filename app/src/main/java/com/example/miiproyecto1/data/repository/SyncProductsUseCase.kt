package com.example.miiproyecto1.data.repository

import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncProductsUseCase @Inject constructor(
    private val db: AppDatabase,
    private val firestoreRepo: FirestoreProductRepository
) {

    suspend fun syncFromFirestoreToRoom() = withContext(Dispatchers.IO) {
        // 1) Obtener lista actual desde Firestore (snapshot único)
        val productsFromFs: List<Product> = firestoreRepo.getAllProductsFlow().first()

        // 2) Limpiar tabla local y reinsertar
        val dao = db.productDao()
        dao.deleteAllProducts()
        dao.upsertProducts(productsFromFs)
    }
}
