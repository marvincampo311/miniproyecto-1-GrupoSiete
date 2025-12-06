package com.example.miiproyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.miiproyecto1.databinding.ActivityMainBinding
import com.example.miiproyecto1.ui.viewmodel.AuthViewModel
import com.example.miiproyecto1.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import android.view.View
import android.graphics.Color

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding  // activity_main.xml = layout del login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay sesión Firebase, ir directo a Home (criterios 10, 14, 16)
        if (authViewModel.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NO usar toolbar aquí (criterio 1)

        setupTextWatchers()
        setupObservers()
        setupClicks()
    }

    private fun setupTextWatchers() {
        // Email: máximo 40 caracteres (criterio 3)
        binding.tilEmail.editText?.addTextChangedListener {
            val email = it?.toString().orEmpty()
            if (email.length > 40) {
                binding.tilEmail.error = "Máximo 40 caracteres"
            } else {
                binding.tilEmail.error = null
            }
            updateButtonsEnabled()
        }

        // Password: solo números, entre 6 y 10, validación en tiempo real (criterio 5)
        binding.tilPassword.editText?.addTextChangedListener { editable ->
            val pass = editable?.toString().orEmpty()

            // Solo dígitos
            if (pass.any { !it.isDigit() }) {
                binding.tilPassword.error = "Solo números"
            } else {
                binding.tilPassword.error = null
            }

            if (pass.length in 1..5) {
                binding.tvPasswordError.visibility = View.VISIBLE
                binding.tilPassword.isErrorEnabled = true
                binding.tilPassword.error = "Mínimo 6 dígitos"
            } else {
                binding.tvPasswordError.visibility = View.GONE
                binding.tilPassword.isErrorEnabled = false
                binding.tilPassword.error = null
            }

            // Forzar máximo 10 dígitos
            if (pass.length > 10) {
                val trimmed = pass.take(10)
                binding.etPassword.setText(trimmed)
                binding.etPassword.setSelection(trimmed.length)
            }

            updateButtonsEnabled()
        }
    }

    // Habilita / deshabilita Login y Registrarse (criterios 7, 8, 11, 12)
    private fun updateButtonsEnabled() {
        val email = binding.etEmail.text?.toString().orEmpty()
        val pass = binding.etPassword.text?.toString().orEmpty()

        val camposLlenos = email.isNotBlank() && pass.isNotBlank()
        val passwordValida = pass.length in 6..10 && pass.all { it.isDigit() }
        val habilitar = camposLlenos && passwordValida

        binding.btnLogin.isEnabled = habilitar
        binding.tvRegister.isEnabled = habilitar

        binding.btnLogin.alpha = if (habilitar) 1f else 0.5f
        // "Registrarse" gris cuando está inactivo, blanco cuando está activo
        val colorActivo = resources.getColor(android.R.color.white, theme)
        val colorInactivo = Color.parseColor("#9EA1A1")
        binding.tvRegister.setTextColor(if (habilitar) colorActivo else colorInactivo)
    }

    // Clicks de Login y Registrarse usando AuthViewModel (criterios 9, 10, 13, 14, 17)
    private fun setupClicks() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            authViewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            authViewModel.register(email, password)
        }
    }

    private fun setupObservers() {
        authViewModel.loading.observe(this) { loading ->
            binding.btnLogin.isEnabled = !loading
            binding.tvRegister.isEnabled = !loading
        }

        authViewModel.loginSuccess.observe(this) { success ->
            if (success == true) {
                // Tanto login como registro exitoso → ir a Home Inventario (criterios 10, 14, 16)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        authViewModel.error.observe(this) { error ->
            error?.let { raw ->
                // Ajustar mensaje según el tipo de error de Firebase (criterios 9 y 13)
                val lower = raw.lowercase()
                val msg = when {
                    // usuario no encontrado o password incorrecta
                    lower.contains("no user record") ||
                            lower.contains("password is invalid") ->
                        "Login incorrecto"
                    // email ya registrado
                    lower.contains("already in use") ->
                        "Error en el registro"
                    else -> raw
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
