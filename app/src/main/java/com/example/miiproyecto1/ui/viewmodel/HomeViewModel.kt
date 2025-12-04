package com.example.miiproyecto1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val db: AppDatabase
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadProducts() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val list = db.productDao().getAllProductsSync()
            _products.postValue(list)
            _loading.postValue(false)
        }
    }

    // ✅ NUEVO: eliminar producto
    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.productDao().deleteProduct(product)
                val list = db.productDao().getAllProductsSync()
                _products.postValue(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

// Factory para el ViewModel
class HomeViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}