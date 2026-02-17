package com.example.appchallengemeli.domain.model

data class ProductDetail(
    val id: String,
    val title: String,
    val price: Double,
    val currencyId: String,
    val pictures: List<String>,
    val condition: String,
    val soldQuantity: Int,
    val availableQuantity: Int,
    val freeShipping: Boolean,
    val warranty: String?,
    val attributes: List<ProductAttribute>,
    val description: String
)

data class ProductAttribute(
    val name: String,
    val value: String?
)
