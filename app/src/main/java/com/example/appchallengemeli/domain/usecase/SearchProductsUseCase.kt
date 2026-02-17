package com.example.appchallengemeli.domain.usecase

import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.domain.model.SearchResult
import com.example.appchallengemeli.domain.repository.ProductRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        query: String,
        offset: Int = 0,
        limit: Int = 20
    ): Result<SearchResult> = repository.searchProducts(query, offset, limit)
}
