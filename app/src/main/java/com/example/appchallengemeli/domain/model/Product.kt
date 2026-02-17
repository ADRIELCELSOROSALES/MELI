package com.example.appchallengemeli.domain.model

data class Product(
    val id: String,
    val title: String,
    val price: Double,
    val currencyId: String,
    val thumbnailUrl: String,
    val condition: String,
    val freeShipping: Boolean,
    val availableQuantity: Int
)
