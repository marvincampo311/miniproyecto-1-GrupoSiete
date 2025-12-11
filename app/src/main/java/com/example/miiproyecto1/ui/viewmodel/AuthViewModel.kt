package com.example.miiproyecto1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miiproyecto1.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Para mostrar / ocultar loading en la UI
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // Indica que hubo login/registro exitoso
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    // Mensaje de error crudo desde Firebase (MainActivity ya lo traduce a Toast adecuado)
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        _loading.value = true
        _error.value = null
        _loginSuccess.value = false

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loading.value = false
            result
                .onSuccess {
                    _loginSuccess.value = true
                }
                .onFailure {
                    _error.value =  "Login incorrecto"
                }
        }
    }

    fun register(email: String, password: String) {
        _loading.value = true
        _error.value = null
        _loginSuccess.value = false

        viewModelScope.launch {
            val result = authRepository.register(email, password)
            _loading.value = false
            result
                .onSuccess {
                    _loginSuccess.value = true
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Error al registrarse"
                }
        }
    }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()   // usado por MainActivity [web:55]

    fun logout() {
        authRepository.logout()
    }
}
