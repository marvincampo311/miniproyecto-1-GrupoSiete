package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.miiproyecto1.databinding.ActivityMainBinding
import com.example.miiproyecto1.ui.viewmodel.AuthViewModel
import com.example.miiproyecto1.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    companion object {
        private const val TAG = "BiometricLogin"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        // 1) Si ya hay sesión Firebase, ir directo a Home
        if (authViewModel.isLoggedIn()) {
            Toast.makeText(this, "Ya hay sesión activa en Firebase", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        testFirebaseLogin()





        // 2) Si tienes sesión local marcada, también ir a Home
//        if (SessionManager.isLoggedIn(this)) {
//            Log.d(TAG, "Sesión local activa, navegando a Home")
//            startActivity(Intent(this, HomeActivity::class.java))
//            finish()
//            return
//        }

        // 3) Mostrar pantalla con biometría
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBiometricAuthentication()
        setupUI()
    }

    private fun setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Error de autenticación: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "¡Autenticación exitosa!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Autenticación exitosa. Navegando a Home.")

                    // Marca sesión local (por ahora) y navega a Home
                    SessionManager.setLoggedIn(this@MainActivity, true)

                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Autenticación fallida.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Autenticación fallida.")
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella digital para iniciar sesión")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun testFirebaseLogin() {
        authViewModel.login("krodownl@gmail.com", "teo12345")

        authViewModel.loginSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Login Firebase exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        authViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Login Firebase fallido: $it", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun setupUI() {
        binding.fingerprintImage.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        Log.d(TAG, "El dispositivo tiene soporte biométrico.")
    }
}
