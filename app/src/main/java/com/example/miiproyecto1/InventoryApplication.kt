package com.example.miiproyecto1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * CLASE INVENTORY APPLICATION - PUNTO DE ENTRADA PARA HILT
 *
 * @HiltAndroidApp: Activa la inyección de dependencias de Hilt en toda la aplicación.
 * Esta anotación genera el contenedor Hilt que proporciona todas las dependencias.
 *
 * IMPORTANTE: Esta clase debe estar definida en el AndroidManifest.xml para que
 * funcione correctamente.
 *
 * Función: Inicializar Hilt y permitir que todas las Activities y Fragments
 * reciban inyecciones de dependencias automáticamente.
 */
@HiltAndroidApp
class InventoryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Aquí puedes inicializar librerías o configuraciones globales si es necesario
        // Por ahora, Hilt se inicializa automáticamente con la anotación @HiltAndroidApp
    }
}
