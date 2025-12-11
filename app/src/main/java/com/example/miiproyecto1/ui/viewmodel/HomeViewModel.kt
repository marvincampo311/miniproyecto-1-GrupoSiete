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
import javax.inject.Inject
import com.example.miiproyecto1.data.repository.FirestoreProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.miiproyecto1.data.repository.SyncProductsUseCase


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FirestoreProductRepository,
    private val syncUseCase: SyncProductsUseCase
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun observeProducts() {
        _loading.value = true
        viewModelScope.launch {
            repo.getAllProductsFlow().collect { list ->
                _products.value = list
                _loading.value = false
            }
        }
    }

    // ✅ NUEVO: eliminar producto
    fun deleteProduct(productId: String) {
        viewModelScope.launch{
            repo.deleteProduct(productId)
        }

    }


    //sincronizacion firebase->room
    fun syncToLocalForWidget() {
        viewModelScope.launch {
            try {
                syncUseCase.syncFromFirestoreToRoom()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}

// Factory para el ViewModel
//class HomeViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return HomeViewModel(database) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
