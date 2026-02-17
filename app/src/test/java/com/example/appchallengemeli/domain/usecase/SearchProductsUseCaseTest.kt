package com.example.appchallengemeli.domain.usecase

import com.example.appchallengemeli.core.AppException
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.domain.model.SearchResult
import com.example.appchallengemeli.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchProductsUseCaseTest {

    private lateinit var repository: ProductRepository
    private lateinit var useCase: SearchProductsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchProductsUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct params`() = runTest {
        val expected = SearchResult("test", 0, emptyList(), 0, 20)
        coEvery { repository.searchProducts("test", 10, 30) } returns Result.Success(expected)

        val result = useCase("test", 10, 30)

        assertTrue(result is Result.Success)
        assertEquals(expected, (result as Result.Success).data)
        coVerify(exactly = 1) { repository.searchProducts("test", 10, 30) }
    }

    @Test
    fun `invoke uses default offset and limit`() = runTest {
        val expected = SearchResult("query", 0, emptyList(), 0, 20)
        coEvery { repository.searchProducts("query", 0, 20) } returns Result.Success(expected)

        val result = useCase("query")

        assertTrue(result is Result.Success)
        coVerify { repository.searchProducts("query", 0, 20) }
    }

    @Test
    fun `invoke returns error from repository`() = runTest {
        coEvery { repository.searchProducts(any(), any(), any()) } returns Result.Error(AppException.Network)

        val result = useCase("test")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.Network)
    }
}
