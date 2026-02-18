package com.example.appchallengemeli.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appchallengemeli.core.AppConfig
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.core.UiState
import com.example.appchallengemeli.domain.model.Product
import com.example.appchallengemeli.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProducts: SearchProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var searchJob: Job? = null
    private var currentOffset = 0
    private var totalResults = 0
    private val accumulatedProducts = mutableListOf<Product>()

    val canLoadMore: Boolean
        get() = currentOffset + AppConfig.DEFAULT_PAGE_LIMIT < totalResults

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun search() {
        val currentQuery = _query.value.trim()
        if (currentQuery.isBlank()) return

        searchJob?.cancel()
        currentOffset = 0
        totalResults = 0
        accumulatedProducts.clear()

        searchJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = searchProducts(currentQuery, 0, AppConfig.DEFAULT_PAGE_LIMIT)) {
                is Result.Success -> {
                    val data = result.data
                    totalResults = data.totalResults
                    accumulatedProducts.addAll(data.products)
                    currentOffset = data.products.size
                    _uiState.value = if (data.products.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(accumulatedProducts.toList())
                    }
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.exception)
                }
            }
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value || !canLoadMore) return
        val currentQuery = _query.value.trim()
        if (currentQuery.isBlank()) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            when (val result = searchProducts(currentQuery, currentOffset, AppConfig.DEFAULT_PAGE_LIMIT)) {
                is Result.Success -> {
                    val data = result.data
                    accumulatedProducts.addAll(data.products)
                    currentOffset += data.products.size
                    _uiState.value = UiState.Success(accumulatedProducts.toList())
                }
                is Result.Error -> {
                    // No reemplazamos el estado actual, solo dejamos de cargar
                }
            }
            _isLoadingMore.value = false
        }
    }
}
