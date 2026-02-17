package com.example.appchallengemeli.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.appchallengemeli.MainDispatcherRule
import com.example.appchallengemeli.core.AppException
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.core.UiState
import com.example.appchallengemeli.domain.model.ProductDetail
import com.example.appchallengemeli.domain.usecase.GetProductDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var getProductDetail: GetProductDetailUseCase

    private fun aProductDetail() = ProductDetail(
        id = "MLA123",
        title = "iPhone 15",
        price = 999.99,
        currencyId = "ARS",
        pictures = listOf("https://img.com/1.jpg"),
        condition = "new",
        soldQuantity = 50,
        availableQuantity = 10,
        freeShipping = true,
        warranty = "12 meses",
        attributes = emptyList(),
        description = "Una descripción"
    )

    @Before
    fun setup() {
        getProductDetail = mockk()
    }

    @Test
    fun `init loads detail and emits Success`() = runTest(testDispatcher) {
        coEvery { getProductDetail("MLA123") } returns Result.Success(aProductDetail())

        val savedStateHandle = SavedStateHandle(mapOf("itemId" to "MLA123"))
        val viewModel = DetailViewModel(savedStateHandle, getProductDetail)

        // Initially Loading
        assertEquals(UiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("MLA123", (state as UiState.Success).data.id)
    }

    @Test
    fun `init loads detail and emits Error on failure`() = runTest(testDispatcher) {
        coEvery { getProductDetail("MLA123") } returns
            Result.Error(AppException.Network)

        val savedStateHandle = SavedStateHandle(mapOf("itemId" to "MLA123"))
        val viewModel = DetailViewModel(savedStateHandle, getProductDetail)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).exception is AppException.Network)
    }

    @Test
    fun `loadDetail retries and emits Success`() = runTest(testDispatcher) {
        coEvery { getProductDetail("MLA123") } returns
            Result.Error(AppException.Network) andThen
            Result.Success(aProductDetail())

        val savedStateHandle = SavedStateHandle(mapOf("itemId" to "MLA123"))
        val viewModel = DetailViewModel(savedStateHandle, getProductDetail)

        advanceUntilIdle()

        // First call from init - error
        assertTrue(viewModel.uiState.value is UiState.Error)

        // Retry
        viewModel.loadDetail()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
    }
}
