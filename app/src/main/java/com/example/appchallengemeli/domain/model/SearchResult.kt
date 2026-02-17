package com.example.appchallengemeli.domain.model

data class SearchResult(
    val query: String,
    val totalResults: Int,
    val products: List<Product>,
    val offset: Int,
    val limit: Int
)
