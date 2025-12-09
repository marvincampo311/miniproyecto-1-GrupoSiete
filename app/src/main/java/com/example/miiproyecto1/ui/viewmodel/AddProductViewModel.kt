package com.example.miiproyecto1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val repository: ProductRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun validateProduct(
        codigo: String,
        name: String,
        price: String,
        cantidad: String
    ): Boolean {
        if (codigo.isBlank() || name.isBlank() || price.isBlank() || cantidad.isBlank()) return false
        if (codigo.length != 4 || !codigo.all { it.isDigit() }) return false
        if (name.length > 40) return false
        if (price.toDoubleOrNull() == null) return false
        if (cantidad.toIntOrNull() == null) return false
        return true
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch(ioDispatcher) {
            try {
                repository.insertProduct(product)
                _saveSuccess.postValue(true)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Error al guardar")
            }
        }
    }
}
