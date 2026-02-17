package com.example.appchallengemeli.data.repository

import com.example.appchallengemeli.core.AppException
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.data.remote.api.MeliApi
import com.example.appchallengemeli.data.remote.dto.ItemDescriptionDto
import com.example.appchallengemeli.data.remote.dto.ItemDetailDto
import com.example.appchallengemeli.data.remote.dto.PagingDto
import com.example.appchallengemeli.data.remote.dto.SearchResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ProductRepositoryImplTest {

    private lateinit var api: MeliApi
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = ProductRepositoryImpl(api)
    }

    @Test
    fun `searchProducts returns Success when API responds`() = runTest {
        val responseDto = SearchResponseDto(
            query = "celular",
            results = emptyList(),
            paging = PagingDto(total = 0, primaryResults = 0, offset = 0, limit = 20)
        )
        coEvery { api.searchProducts("celular", 0, 20) } returns responseDto

        val result = repository.searchProducts("celular")

        assertTrue(result is Result.Success)
        assertEquals("celular", (result as Result.Success).data.query)
    }

    @Test
    fun `searchProducts returns Network error on IOException`() = runTest {
        coEvery { api.searchProducts(any(), any(), any()) } throws IOException("No connection")

        val result = repository.searchProducts("test")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.Network)
    }

    @Test
    fun `searchProducts returns TokenExpired on 401`() = runTest {
        val response = Response.error<Any>(401, "Unauthorized".toResponseBody())
        coEvery { api.searchProducts(any(), any(), any()) } throws HttpException(response)

        val result = repository.searchProducts("test")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.TokenExpired)
    }

    @Test
    fun `searchProducts returns Api error with parsed message on 403`() = runTest {
        val errorJson = """{"message":"At least one policy returned UNAUTHORIZED.","code":"PA_UNAUTHORIZED","blocked_by":"PolicyAgent","status":403}"""
        val response = Response.error<Any>(403, errorJson.toResponseBody())
        coEvery { api.searchProducts(any(), any(), any()) } throws HttpException(response)

        val result = repository.searchProducts("test")

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).exception as AppException.Api
        assertEquals(403, error.code)
        assertTrue(error.msg.contains("PolicyAgent"))
    }

    @Test
    fun `searchProducts returns Api error on other HTTP errors`() = runTest {
        val response = Response.error<Any>(500, "Server Error".toResponseBody())
        coEvery { api.searchProducts(any(), any(), any()) } throws HttpException(response)

        val result = repository.searchProducts("test")

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).exception as AppException.Api
        assertEquals(500, error.code)
    }

    @Test
    fun `getProductDetail returns Success combining detail and description`() = runTest {
        val detailDto = ItemDetailDto(
            id = "MLA123",
            title = "iPhone",
            price = 999.0,
            currencyId = "ARS",
            pictures = emptyList(),
            condition = "new",
            soldQuantity = 10,
            availableQuantity = 5,
            shipping = null,
            warranty = null,
            attributes = emptyList()
        )
        val descriptionDto = ItemDescriptionDto(plainText = "Descripción")

        coEvery { api.getItemDetail("MLA123") } returns detailDto
        coEvery { api.getItemDescription("MLA123") } returns descriptionDto

        val result = repository.getProductDetail("MLA123")

        assertTrue(result is Result.Success)
        val detail = (result as Result.Success).data
        assertEquals("MLA123", detail.id)
        assertEquals("Descripción", detail.description)
    }

    @Test
    fun `getProductDetail returns Error when detail call fails`() = runTest {
        coEvery { api.getItemDetail(any()) } throws IOException("fail")
        coEvery { api.getItemDescription(any()) } returns ItemDescriptionDto("text")

        val result = repository.getProductDetail("MLA123")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AppException.Network)
    }
}
