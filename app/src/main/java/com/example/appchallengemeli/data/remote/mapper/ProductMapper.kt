package com.example.appchallengemeli.data.remote.mapper

import com.example.appchallengemeli.data.remote.dto.AttributeDto
import com.example.appchallengemeli.data.remote.dto.ItemDescriptionDto
import com.example.appchallengemeli.data.remote.dto.ItemDetailDto
import com.example.appchallengemeli.data.remote.dto.ProductDto
import com.example.appchallengemeli.data.remote.dto.SearchResponseDto
import com.example.appchallengemeli.domain.model.Product
import com.example.appchallengemeli.domain.model.ProductAttribute
import com.example.appchallengemeli.domain.model.ProductDetail
import com.example.appchallengemeli.domain.model.SearchResult

fun SearchResponseDto.toDomain(): SearchResult = SearchResult(
    query = query.orEmpty(),
    totalResults = paging.total,
    products = results.map { it.toDomain() },
    offset = paging.offset,
    limit = paging.limit
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    price = price ?: 0.0,
    currencyId = currencyId.orEmpty(),
    thumbnailUrl = thumbnail?.replace("http://", "https://").orEmpty(),
    condition = condition.orEmpty(),
    freeShipping = shipping?.freeShipping ?: false,
    availableQuantity = availableQuantity ?: 0
)

fun ItemDetailDto.toDomain(description: String): ProductDetail = ProductDetail(
    id = id,
    title = title,
    price = price ?: 0.0,
    currencyId = currencyId.orEmpty(),
    pictures = pictures?.mapNotNull { it.secureUrl }.orEmpty(),
    condition = condition.orEmpty(),
    soldQuantity = soldQuantity ?: 0,
    availableQuantity = availableQuantity ?: 0,
    freeShipping = shipping?.freeShipping ?: false,
    warranty = warranty,
    attributes = attributes?.map { it.toDomain() }.orEmpty(),
    description = description
)

fun AttributeDto.toDomain(): ProductAttribute = ProductAttribute(
    name = name.orEmpty(),
    value = valueName
)

fun ItemDescriptionDto.toDomain(): String = plainText.orEmpty()
