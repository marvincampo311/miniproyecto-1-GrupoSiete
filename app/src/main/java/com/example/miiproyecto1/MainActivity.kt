package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.miiproyecto1.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BiometricLogin"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBiometricAuthentication()
        setupUI()
    }

    private fun setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Error de autenticación: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "¡Autenticación exitosa!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Autenticación exitosa. Navegando a Home.")

                    // ✅ CORRECTO: HomeActivity (no HomeFragment)
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Autenticación fallida.", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Autenticación fallida.")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella digital para iniciar sesión")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun setupUI() {
        binding.fingerprintImage.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        Log.d(TAG, "El dispositivo tiene soporte biométrico.")
    }
}
