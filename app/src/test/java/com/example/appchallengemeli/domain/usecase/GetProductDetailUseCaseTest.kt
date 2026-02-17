package com.example.appchallengemeli.domain.usecase

import com.example.appchallengemeli.core.AppException
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.domain.model.ProductDetail
import com.example.appchallengemeli.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetProductDetailUseCaseTest {

    private lateinit var repository: ProductRepository
    private lateinit var useCase: GetProductDetailUseCase

    private fun aProductDetail() = ProductDetail(
        id = "MLA123",
        title = "iPhone 15",
        price = 999.99,
        currencyId = "ARS",
        pictures = listOf("https://img.com/1.jpg"),
        condition = "new",
        soldQuantity = 50,
        availableQuantity = 10,
        freeShipping = true,
        warranty = "12 meses",
        attributes = emptyList(),
        description = "Una descripción"
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetProductDetailUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct itemId`() = runTest {
        val expected = aProductDetail()
        coEvery { repository.getProductDetail("MLA123") } returns Result.Success(expected)

        val result = useCase("MLA123")

        assertTrue(result is Result.Success)
        assertEquals(expected, (result as Result.Success).data)
        coVerify(exactly = 1) { repository.getProductDetail("MLA123") }
    }

    @Test
    fun `invoke returns error from repository`() = runTest {
        coEvery { repository.getProductDetail(any()) } returns Result.Error(AppException.Network)

        val result = useCase("MLA123")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.Network)
    }
}
