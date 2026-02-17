package com.example.appchallengemeli.domain.repository

import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.domain.model.ProductDetail
import com.example.appchallengemeli.domain.model.SearchResult

interface ProductRepository {
    suspend fun searchProducts(query: String, offset: Int = 0, limit: Int = 20): Result<SearchResult>
    suspend fun getProductDetail(itemId: String): Result<ProductDetail>
}
