package com.example.miiproyecto1.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.repository.FirestoreProductRepository
import com.example.miiproyecto1.data.repository.SyncProductsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // Ejecuta LiveData de forma síncrona
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    private val repository: FirestoreProductRepository = mock()
    private val syncUseCase: SyncProductsUseCase = mock()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        viewModel = HomeViewModel(repository, syncUseCase)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    // ---------------------------------------------------------
    // observeProducts
    // ---------------------------------------------------------

    @Test
    fun `observeProducts updates products and loading`() = runTest {
        val productList = listOf(
            Product(
                codigo = "1234",
                name = "Producto 1",
                price = 1.000,
                cantidad = 2
            ),
            Product(
                codigo = "5678",
                name = "Producto 2",
                price = 2.000,
                cantidad = 5
            )
        )

        whenever(repository.getAllProductsFlow()).thenReturn(flowOf(productList))

        val productsObserver: Observer<List<Product>> = mock()
        val loadingObserver: Observer<Boolean> = mock()

        viewModel.products.observeForever(productsObserver)
        viewModel.loading.observeForever(loadingObserver)

        viewModel.observeProducts()
        dispatcher.scheduler.advanceUntilIdle()

        verify(loadingObserver).onChanged(true)
        verify(productsObserver).onChanged(productList)
        verify(loadingObserver).onChanged(false)
    }

    // ---------------------------------------------------------
    // deleteProduct
    // ---------------------------------------------------------

    @Test
    fun `deleteProduct calls repository deleteProduct`() = runTest {
        val productId = "abc123"

        viewModel.deleteProduct(productId)
        dispatcher.scheduler.advanceUntilIdle()

        verify(repository).deleteProduct(productId)
    }

    // ---------------------------------------------------------
    // syncToLocalForWidget - éxito
    // ---------------------------------------------------------

    @Test
    fun `syncToLocalForWidget calls sync use case`() = runTest {
        viewModel.syncToLocalForWidget()
        dispatcher.scheduler.advanceUntilIdle()

        verify(syncUseCase).syncFromFirestoreToRoom()
    }

    // ---------------------------------------------------------
    // syncToLocalForWidget - error (catch branch)
    // ---------------------------------------------------------

    @Test
    fun `syncToLocalForWidget handles exception gracefully`() = runTest {
        whenever(syncUseCase.syncFromFirestoreToRoom())
            .thenThrow(RuntimeException("Error de sync"))

        viewModel.syncToLocalForWidget()
        dispatcher.scheduler.advanceUntilIdle()

        verify(syncUseCase).syncFromFirestoreToRoom()
        // No crash = test pasa ✅
    }
}
