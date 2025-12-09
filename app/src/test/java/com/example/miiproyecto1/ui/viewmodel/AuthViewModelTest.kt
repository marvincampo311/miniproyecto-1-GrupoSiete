package com.example.miiproyecto1.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.miiproyecto1.data.auth.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val repository: AuthRepository = mock()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    // --------------------------------------------------------
    // LOGIN
    // --------------------------------------------------------

    @Test
    fun `login sets loginSuccess true when repository returns success`() = runTest {
        whenever(repository.login(any(), any()))
            .thenReturn(Result.success(Unit))

        val observer: Observer<Boolean> = mock()
        viewModel.loginSuccess.observeForever(observer)

        viewModel.login("test@email.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(true)
    }

    @Test
    fun `login sets error when repository returns failure`() = runTest {
        whenever(repository.login(any(), any()))
            .thenReturn(Result.failure(RuntimeException("Credenciales inválidas")))

        val observer: Observer<String?> = mock()
        viewModel.error.observeForever(observer)

        viewModel.login("test@email.com", "wrong")
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged("Credenciales inválidas")
    }

    // --------------------------------------------------------
    // REGISTER
    // --------------------------------------------------------

    @Test
    fun `register sets loginSuccess true when repository returns success`() = runTest {
        whenever(repository.register(any(), any()))
            .thenReturn(Result.success(Unit))

        val observer: Observer<Boolean> = mock()
        viewModel.loginSuccess.observeForever(observer)

        viewModel.register("test@email.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged(true)
    }

    @Test
    fun `register sets error when repository returns failure`() = runTest {
        whenever(repository.register(any(), any()))
            .thenReturn(Result.failure(RuntimeException("Usuario ya existe")))

        val observer: Observer<String?> = mock()
        viewModel.error.observeForever(observer)

        viewModel.register("test@email.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(observer).onChanged("Usuario ya existe")
    }

    // --------------------------------------------------------
    // MÉTODOS DIRECTOS
    // --------------------------------------------------------

    @Test
    fun `isLoggedIn returns value from repository`() {
        whenever(repository.isLoggedIn()).thenReturn(true)

        val result = viewModel.isLoggedIn()

        Assert.assertTrue(result)
    }

    @Test
    fun `logout calls repository logout`() {
        viewModel.logout()

        verify(repository).logout()
    }
}
