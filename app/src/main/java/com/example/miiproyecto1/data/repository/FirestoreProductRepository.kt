package com.example.miiproyecto1.data.repository

import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.remote.FsProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@Singleton
class FirestoreProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun userProductsCollection() =
        firestore.collection("users")
            .document(requireNotNull(auth.currentUser?.uid) {
                "Usuario no autenticado"
            })
            .collection("products")

    // Mapeo de Product (Room/domino) -> Firestore
    private fun Product.toFs(): Map<String, Any> = mapOf(
        "codigo" to codigo,
        "name" to name,
        "price" to price,
        "cantidad" to cantidad
    )

    // Mapeo de Firestore -> Product (usando remoteId)
    private fun FsProduct.toDomain(docId: String): Product = Product(
        id = 0,               // ID local de Room (0 cuando viene de Firestore)
        remoteId = docId,     // documentId de Firestore
        codigo = codigo,
        name = name,
        price = price,
        cantidad = cantidad
    )

    // Flow reactivo de todos los productos del usuario
    fun getAllProductsFlow(): Flow<List<Product>> = callbackFlow {
        val registration = userProductsCollection()
            .orderBy("codigo", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FsProduct::class.java)?.toDomain(doc.id)
                    }
                    trySend(list)
                }
            }
        awaitClose { registration.remove() }
    }

    // Inserta en Firestore y devuelve el documentId
    suspend fun insertProduct(product: Product): String {
        val data = product.toFs()
        val docRef = userProductsCollection().add(data).await()
        return docRef.id
    }

    // Obtiene un producto por su remoteId (documentId)
    suspend fun getProductById(remoteId: String): Product? {
        val snap = userProductsCollection().document(remoteId).get().await()
        val fs = snap.toObject(FsProduct::class.java) ?: return null
        return fs.toDomain(snap.id)
    }

    // Actualiza un producto en Firestore por remoteId
    suspend fun updateProduct(remoteId: String, product: Product) {
        userProductsCollection().document(remoteId).set(product.toFs()).await()
    }

    // Elimina un producto en Firestore por remoteId
    suspend fun deleteProduct(remoteId: String) {
        userProductsCollection().document(remoteId).delete().await()
    }

    // Total del inventario (single shot)
    suspend fun getTotalInventoryValue(): Double {
        val snap = userProductsCollection().get().await()
        var total = 0.0
        for (doc in snap.documents) {
            val p = doc.toObject(FsProduct::class.java) ?: continue
            total += p.price * p.cantidad
        }
        return total
    }

    // Total del inventario como Flow
    fun getTotalInventoryValueFlow(): Flow<Double> =
        getAllProductsFlow().map { list ->
            list.sumOf { it.price * it.cantidad }
        }
}
