package com.example.miiproyecto1.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.miiproyecto1.data.auth.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mock()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    // --------------------------------------------------------
    // LOGIN
    // --------------------------------------------------------

    @Test
    fun `login sets loading true then false and success true on success`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.success(Unit))

        val loadingObserver = mock<Observer<Boolean>>()
        val successObserver = mock<Observer<Boolean>>()

        viewModel.loading.observeForever(loadingObserver)
        viewModel.loginSuccess.observeForever(successObserver)

        viewModel.login("test@test.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)
        verify(successObserver).onChanged(false)
        verify(successObserver).onChanged(true)
    }

    @Test
    fun `login sets error when repository returns failure`() = runTest {
        whenever(authRepository.login(any(), any()))
            .thenReturn(Result.failure(Exception("firebase error")))

        val errorObserver = mock<Observer<String?>>()
        viewModel.error.observeForever(errorObserver)

        viewModel.login("test@test.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(errorObserver).onChanged(null)
        verify(errorObserver).onChanged("Login incorrecto")
    }

    // --------------------------------------------------------
    // REGISTER
    // --------------------------------------------------------

    @Test
    fun `register sets success true on success`() = runTest {
        whenever(authRepository.register(any(), any()))
            .thenReturn(Result.success(Unit))

        val successObserver = mock<Observer<Boolean>>()
        viewModel.loginSuccess.observeForever(successObserver)

        viewModel.register("test@test.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(successObserver).onChanged(false)
        verify(successObserver).onChanged(true)
    }

    @Test
    fun `register sets error message on failure`() = runTest {
        whenever(authRepository.register(any(), any()))
            .thenReturn(Result.failure(Exception("Email ya existe")))

        val errorObserver = mock<Observer<String?>>()
        viewModel.error.observeForever(errorObserver)

        viewModel.register("test@test.com", "123456")
        dispatcher.scheduler.advanceUntilIdle()

        verify(errorObserver).onChanged(null)
        verify(errorObserver).onChanged("Email ya existe")
    }

    // --------------------------------------------------------
    // SESSION
    // --------------------------------------------------------

    @Test
    fun `isLoggedIn returns repository value`() {
        whenever(authRepository.isLoggedIn()).thenReturn(true)

        val result = viewModel.isLoggedIn()

        Assert.assertTrue(result)
        verify(authRepository).isLoggedIn()
    }

    @Test
    fun `logout calls repository logout`() {
        viewModel.logout()

        verify(authRepository).logout()
    }
}
