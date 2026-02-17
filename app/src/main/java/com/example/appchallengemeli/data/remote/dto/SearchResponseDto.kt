package com.example.appchallengemeli.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("query") val query: String?,
    @SerializedName("results") val results: List<ProductDto>,
    @SerializedName("paging") val paging: PagingDto
)

data class PagingDto(
    @SerializedName("total") val total: Int,
    @SerializedName("primary_results") val primaryResults: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int
)

data class ProductDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double?,
    @SerializedName("currency_id") val currencyId: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("condition") val condition: String?,
    @SerializedName("shipping") val shipping: ShippingDto?,
    @SerializedName("available_quantity") val availableQuantity: Int?
)

data class ShippingDto(
    @SerializedName("free_shipping") val freeShipping: Boolean?
)
