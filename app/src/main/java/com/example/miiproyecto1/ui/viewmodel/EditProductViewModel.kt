package com.example.miiproyecto1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.repository.FirestoreProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val repo: FirestoreProductRepository
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun loadProduct(remoteId: String) {
        viewModelScope.launch {
            try {
                val prod = repo.getProductById(remoteId)
                _product.value = prod
            } catch (e: Exception) {
                _product.value = null
            }
        }
    }

    fun validateFields(codigo: String, name: String, price: String, cantidad: String): Boolean {
        if (codigo.length != 4) return false
        if (name.isBlank() || price.isBlank() || cantidad.isBlank()) return false
        if (price.toDoubleOrNull() == null) return false
        if (cantidad.toIntOrNull() == null) return false
        return true
    }

    fun updateProduct(remoteId: String, product: Product) {
        viewModelScope.launch {
            try {
                repo.updateProduct(remoteId, product)
                _updateSuccess.value = true
            } catch (e: Exception) {
                _updateSuccess.value = false
            }
        }
    }
}
