package com.example.miiproyecto1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miiproyecto1.data.local.AppDatabase
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadProducts() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // usamos Flow del repository y tomamos el valor actual
                val list = repository.getAllProductsFlow()
                    .first()   // necesitas: import kotlinx.coroutines.flow.first
                _products.postValue(list)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteProduct(product)
                val list = repository.getAllProductsFlow().first()
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
            return HomeViewModel(ProductRepository(database.productDao())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}