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

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loading.value = false
            result.onSuccess {
                _loginSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Error al iniciar sesión"
            }
        }
    }

    fun register(email: String, password: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = authRepository.register(email, password)
            _loading.value = false
            result.onSuccess {
                _loginSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Error al registrarse"
            }
        }
    }

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

     fun logout() {
        // Simplemente llama a la función suspendida del repositorio
        authRepository.logout()
    }
}
