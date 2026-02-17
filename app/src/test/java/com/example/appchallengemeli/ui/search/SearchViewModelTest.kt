package com.example.appchallengemeli.ui.search

import com.example.appchallengemeli.MainDispatcherRule
import com.example.appchallengemeli.core.AppException
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.core.UiState
import com.example.appchallengemeli.domain.model.Product
import com.example.appchallengemeli.domain.model.SearchResult
import com.example.appchallengemeli.domain.usecase.SearchProductsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var searchProducts: SearchProductsUseCase
    private lateinit var viewModel: SearchViewModel

    private fun aProduct(id: String = "MLA1") = Product(
        id = id,
        title = "Test Product",
        price = 100.0,
        currencyId = "ARS",
        thumbnailUrl = "https://example.com/img.jpg",
        condition = "new",
        freeShipping = true,
        availableQuantity = 5
    )

    @Before
    fun setup() {
        searchProducts = mockk()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        viewModel = SearchViewModel(searchProducts)
        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `search emits Success with products`() = runTest {
        val products = listOf(aProduct("MLA1"), aProduct("MLA2"))
        val searchResult = SearchResult("celular", 2, products, 0, 20)
        coEvery { searchProducts("celular", 0, 20) } returns Result.Success(searchResult)

        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("celular")
        viewModel.search()

        val state = viewModel.uiState.value
        assertTrue("Expected Success but got $state", state is UiState.Success)
        assertEquals(2, (state as UiState.Success).data.size)
    }

    @Test
    fun `search with empty results emits Empty`() = runTest {
        val searchResult = SearchResult("xyzxyz", 0, emptyList(), 0, 20)
        coEvery { searchProducts("xyzxyz", 0, 20) } returns Result.Success(searchResult)

        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("xyzxyz")
        viewModel.search()

        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `search with error emits Error`() = runTest {
        coEvery { searchProducts(any(), any(), any()) } returns
            Result.Error(AppException.Network)

        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("test")
        viewModel.search()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).exception is AppException.Network)
    }

    @Test
    fun `search with blank query does nothing`() = runTest {
        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("   ")
        viewModel.search()

        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `loadMore appends products to existing list`() = runTest {
        val firstPage = listOf(aProduct("MLA1"), aProduct("MLA2"))
        val secondPage = listOf(aProduct("MLA3"), aProduct("MLA4"))
        coEvery { searchProducts("celular", 0, 20) } returns
            Result.Success(SearchResult("celular", 100, firstPage, 0, 20))
        coEvery { searchProducts("celular", 2, 20) } returns
            Result.Success(SearchResult("celular", 100, secondPage, 2, 20))

        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("celular")
        viewModel.search()
        viewModel.loadMore()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(4, (state as UiState.Success).data.size)
    }

    @Test
    fun `loadMore does nothing when no more results`() = runTest {
        val products = listOf(aProduct("MLA1"))
        coEvery { searchProducts("celular", 0, 20) } returns
            Result.Success(SearchResult("celular", 1, products, 0, 20))

        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("celular")
        viewModel.search()

        assertFalse(viewModel.canLoadMore)
    }

    @Test
    fun `new search resets pagination`() = runTest {
        val firstResults = listOf(aProduct("MLA1"), aProduct("MLA2"))
        val newResults = listOf(aProduct("MLA3"))
        coEvery { searchProducts("celular", 0, 20) } returns
            Result.Success(SearchResult("celular", 100, firstResults, 0, 20))
        coEvery { searchProducts("laptop", 0, 20) } returns
            Result.Success(SearchResult("laptop", 1, newResults, 0, 20))

        viewModel = SearchViewModel(searchProducts)
        viewModel.onQueryChanged("celular")
        viewModel.search()
        viewModel.onQueryChanged("laptop")
        viewModel.search()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(1, (state as UiState.Success).data.size)
        assertEquals("MLA3", (state as UiState.Success).data[0].id)
    }
}
