package com.example.miiproyecto1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ✅ Recibe AppDatabase directamente
class EditProductViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun loadProduct(productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prod = database.productDao().getProductById(productId)
                _product.postValue(prod)
            } catch (e: Exception) {
                e.printStackTrace()
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

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                database.productDao().updateProduct(product)
                _updateSuccess.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
