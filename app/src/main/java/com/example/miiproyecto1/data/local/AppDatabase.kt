package com.example.miiproyecto1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * APP DATABASE - CONTENEDOR DE LA BASE DE DATOS ROOM
 *
 * @Database: Indica que esta clase es la base de datos principal
 * entities: Lista de entidades (tablas) en la BD
 * version: Versión actual del esquema (incrementar si cambias las entidades)
 * exportSchema: false para evitar guardar versiones anteriores
 */
@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Abstract function que proporciona acceso al ProductDao
     * Room genera automáticamente la implementación
     */
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * GET DATABASE - Obtiene la instancia única de la BD (Singleton Pattern)
         *
         * @Volatile: Garantiza que la variable se actualiza en todos los hilos
         * synchronized: Evita que se creen múltiples instancias simultáneamente
         *
         * return: Instancia única de AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}