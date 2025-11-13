package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.example.miiproyecto1.databinding.ActivityMainBinding

/**
 * Actividad Principal (Ventana Login)
 * Implementa la autenticación biométrica (Huella Dactilar) para acceder a HomeActivity.
 * Criterios HU 2.0: Fondo oscuro, logo, título "Inventory" naranja, huella dinámica.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo // NOTA: Esto es correcto

    // Etiqueta para logs
    private val TAG = "BiometricLogin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Asocia el listener de clic al botón de la huella
        // Se asume que el layout activity_main.xml tiene un ImageView con ID: @id/fingerprint_icon
        val fingerprintIcon = binding.fingerprintImage
        fingerprintIcon.setOnClickListener {
            // Inicia la autenticación cuando se toca el icono de la huella
            checkBiometricSupportAndAuthenticate()
        }
    }

    /**
     * 1. Verifica si el dispositivo soporta biometría y si hay huellas registradas.
     * 2. Si es compatible, procede con la autenticación.
     */
    private fun checkBiometricSupportAndAuthenticate() {
        val biometricManager = BiometricManager.from(this)
        val status = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)

        when (status) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "El dispositivo tiene soporte biométrico.")
                setupBiometricPrompt()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "El dispositivo NO tiene hardware de biometría.", Toast.LENGTH_LONG).show()
                Log.e(TAG, "BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "El hardware de biometría no está disponible.", Toast.LENGTH_LONG).show()
                Log.e(TAG, "BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No hay huellas dactilares registradas. Por favor, registre una en la configuración.", Toast.LENGTH_LONG).show()
                Log.e(TAG, "BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED")
            }
            else -> {
                Toast.makeText(this, "Error de soporte biométrico desconocido: $status", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: $status")
            }
        }
    }

    /**
     * 2. Configura el BiometricPrompt y el PromptInfo (títulos de la ventana emergente).
     * Criterio 5: Título, subtítulo y botón Cancelar.
     */
    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Criterio 6: Mostrar mensaje de error si la huella es incorrecta o cancelada
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_CANCELED) {
                        Toast.makeText(applicationContext, "Error de autenticación: $errString", Toast.LENGTH_SHORT).show()
                    }
                    Log.d(TAG, "Autenticación fallida: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "¡Autenticación exitosa!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Autenticación exitosa. Navegando a Home.")

                    // Criterio 6: Si la huella es correcta, dirige a HU 3.0 Ventana Home Inventario
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra la actividad de Login
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Huella no reconocida. Intente de nuevo.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Huella no reconocida.")
                }
            })

        // Configuración de la ventana emergente (PromptInfo)
        promptInfo = BiometricPrompt.PromptInfo.Builder() // ESTA LÍNEA ES CORRECTA
            .setTitle("Autenticación con Biometría") // Criterio 5: Título
            .setSubtitle("Ingrese su huella digital") // Criterio 5: Subtítulo
            .setNegativeButtonText("Cancelar") // Criterio 5: Botón Cancelar
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG) // Solo huella/rostro
            .build()
    }
}