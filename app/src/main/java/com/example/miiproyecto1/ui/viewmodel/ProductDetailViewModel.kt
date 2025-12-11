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
class ProductDetailViewModel @Inject constructor(
    private val repo: FirestoreProductRepository
) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadProduct(productId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val p = repo.getProductById(productId)
                _product.value = p
            } catch (e: Exception) {
                _product.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                repo.deleteProduct(productId)
                _deleteSuccess.value = true
            } catch (e: Exception) {
                _deleteSuccess.value = false
            }
        }
    }
}


//hola