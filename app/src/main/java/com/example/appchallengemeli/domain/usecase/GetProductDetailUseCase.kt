package com.example.appchallengemeli.domain.usecase

import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.domain.model.ProductDetail
import com.example.appchallengemeli.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductDetailUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(itemId: String): Result<ProductDetail> =
        repository.getProductDetail(itemId)
}
