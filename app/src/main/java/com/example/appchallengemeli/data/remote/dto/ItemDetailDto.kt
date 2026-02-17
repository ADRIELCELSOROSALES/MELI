package com.example.appchallengemeli.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ItemDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: Double?,
    @SerializedName("currency_id") val currencyId: String?,
    @SerializedName("pictures") val pictures: List<PictureDto>?,
    @SerializedName("condition") val condition: String?,
    @SerializedName("sold_quantity") val soldQuantity: Int?,
    @SerializedName("available_quantity") val availableQuantity: Int?,
    @SerializedName("shipping") val shipping: ShippingDto?,
    @SerializedName("warranty") val warranty: String?,
    @SerializedName("attributes") val attributes: List<AttributeDto>?
)

data class PictureDto(
    @SerializedName("id") val id: String?,
    @SerializedName("secure_url") val secureUrl: String?
)

data class AttributeDto(
    @SerializedName("name") val name: String?,
    @SerializedName("value_name") val valueName: String?
)
