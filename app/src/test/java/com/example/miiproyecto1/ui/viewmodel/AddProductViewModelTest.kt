package com.example.miiproyecto1.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.miiproyecto1.data.local.Product
import com.example.miiproyecto1.data.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val repository: ProductRepository = mock()
    private lateinit var viewModel: AddProductViewModel

    @Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        viewModel = AddProductViewModel(repository, dispatcher)   // << INYECTA DISPATCHER
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    // --------------------------------------------------------
    // TESTS DE VALIDACIONES
    // --------------------------------------------------------

    @Test
    fun `validateProduct returns true for valid input`() {
        val result = viewModel.validateProduct(
            codigo = "1234",
            name = "Producto",
            price = "1500",
            cantidad = "10"
        )
        Assert.assertTrue(result)
    }

    @Test
    fun `validateProduct returns false when any field is blank`() {
        val result = viewModel.validateProduct(
            codigo = "",
            name = "Producto",
            price = "1500",
            cantidad = "10"
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `validateProduct returns false for invalid code length`() {
        val result = viewModel.validateProduct(
            codigo = "12",
            name = "Producto",
            price = "1500",
            cantidad = "10"
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `validateProduct returns false for non-digit code`() {
        val result = viewModel.validateProduct(
            codigo = "12A4",
            name = "Producto",
            price = "1500",
            cantidad = "10"
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `validateProduct returns false when name exceeds 40 characters`() {
        val result = viewModel.validateProduct(
            codigo = "1234",
            name = "A".repeat(41),
            price = "1500",
            cantidad = "10"
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `validateProduct returns false when price is not numeric`() {
        val result = viewModel.validateProduct(
            codigo = "1234",
            name = "Producto",
            price = "abc",
            cantidad = "10"
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `validateProduct returns false when cantidad is not numeric`() {
        val result = viewModel.validateProduct(
            codigo = "1234",
            name = "Producto",
            price = "1500",
            cantidad = "x10"
        )
        Assert.assertFalse(result)
    }

    // --------------------------------------------------------
    // TEST DE saveProduct (ÉXITO)
    // --------------------------------------------------------

    @Test
    fun `saveProduct calls repository insertProduct`() = runTest {
        val product = Product(
            codigo = "1234",
            name = "Producto X",
            price = 1500.0,
            cantidad = 5
        )

        viewModel.saveProduct(product)
        advanceUntilIdle()

        verify(repository).insertProduct(product)
    }

    // --------------------------------------------------------
    // TEST DE saveProduct (ERROR EN REPOSITORY)
    // --------------------------------------------------------

    @Test
    fun `saveProduct posts error when repository throws exception`() = runTest {
        val product = Product(
            codigo = "1234",
            name = "Producto X",
            price = 1500.0,
            cantidad = 5
        )

        whenever(repository.insertProduct(any())).thenThrow(RuntimeException("Fallo en DB"))

        val observer: Observer<String> = mock()
        viewModel.error.observeForever(observer)

        viewModel.saveProduct(product)
        advanceUntilIdle()

        verify(observer).onChanged(eq("Fallo en DB"))
    }
}
