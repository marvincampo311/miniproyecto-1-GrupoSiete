package com.example.miiproyecto1.di

import android.content.Context
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.ProductDao
import com.example.miiproyecto1.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.miiproyecto1.data.repository.FirestoreProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


/**
 * DATABASE MODULE - MÓDULO DE INYECCIÓN DE DEPENDENCIAS (HILT)
 *
 * RESPONSABILIDAD: Proveer las instancias de:
 * 1. AppDatabase - Base de datos Room (Singleton)
 * 2. ProductDao - Acceso a datos (Singleton)
 * 3. ProductRepository - Lógica de repositorio (Singleton)
 *
 * @Module: Indica a Hilt que este es un módulo de configuración
 * @InstallIn(SingletonComponent::class): Disponible en toda la aplicación
 *
 * NOTA: Las advertencias "never used" son normales en Hilt porque
 * estas funciones se invocan mediante reflection (no directamente en el código)
 */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")  // ✅ SUPRIME LAS ADVERTENCIAS DEL IDE
object DatabaseModule {

    /**
     * PROVIDE APP DATABASE - Crea y proporciona la instancia de Room
     *
     * @Singleton: Solo se crea una instancia en toda la app
     * @Provides: Hilt usa este método para crear AppDatabase
     * @ApplicationContext: Android proporciona automáticamente el contexto
     *
     * @return AppDatabase - La instancia única de la base de datos
     */
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    /**
     * PROVIDE PRODUCT DAO - Extrae el DAO de la base de datos
     *
     * @Singleton: La misma instancia se usa en toda la app
     * @Provides: Hilt usa este método para crear ProductDao
     * appDatabase: Hilt inyecta automáticamente la AppDatabase creada arriba
     *
     * @return ProductDao - El DAO para acceso a productos
     */
    @Singleton
    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    /**
     * PROVIDE PRODUCT REPOSITORY - Crea la instancia del Repositorio
     *
     * @Singleton: Solo una instancia en toda la app
     * @Provides: Hilt usa este método para crear ProductRepository
     * productDao: Hilt inyecta automáticamente el DAO creado arriba
     *
     * @return ProductRepository - El repositorio para acceso a datos
     */
    @Singleton
    @Provides
    fun provideProductRepository(productDao: ProductDao): ProductRepository {
        return ProductRepository(productDao)
    }

    @Singleton
    @Provides
    fun provideFirestoreProductRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirestoreProductRepository =
        FirestoreProductRepository(firestore, auth)
}
