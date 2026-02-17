package com.example.appchallengemeli.data.repository

import com.example.appchallengemeli.core.AppException
import com.example.appchallengemeli.core.Result
import com.example.appchallengemeli.data.remote.api.MeliApi
import com.example.appchallengemeli.data.remote.mapper.toDomain
import com.example.appchallengemeli.domain.model.ProductDetail
import com.example.appchallengemeli.domain.model.SearchResult
import com.example.appchallengemeli.domain.repository.ProductRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: MeliApi
) : ProductRepository {

    override suspend fun searchProducts(
        query: String,
        offset: Int,
        limit: Int
    ): Result<SearchResult> = safeApiCall {
        api.searchProducts(query, offset, limit).toDomain()
    }

    override suspend fun getProductDetail(itemId: String): Result<ProductDetail> = safeApiCall {
        coroutineScope {
            val detailDeferred = async { api.getItemDetail(itemId) }
            val descriptionDeferred = async { api.getItemDescription(itemId) }

            val detail = detailDeferred.await()
            val description = descriptionDeferred.await().toDomain()
            detail.toDomain(description)
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = try {
        Result.Success(call())
    } catch (e: SocketTimeoutException) {
        Result.Error(AppException.Timeout)
    } catch (e: IOException) {
        Result.Error(AppException.Network)
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string().orEmpty()
        val apiMessage = parseApiMessage(errorBody)
        when (e.code()) {
            401 -> Result.Error(AppException.TokenExpired)
            403 -> Result.Error(AppException.Api(403, apiMessage))
            else -> Result.Error(AppException.Api(e.code(), apiMessage))
        }
    } catch (e: Exception) {
        Result.Error(AppException.Unknown(e))
    }

    private fun parseApiMessage(errorBody: String): String = try {
        val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
        buildString {
            json.get("message")?.asString?.let { append(it) }
            json.get("code")?.asString?.let { append(" [$it]") }
            json.get("blocked_by")?.asString?.let { append(" (blocked by: $it)") }
        }.ifEmpty { errorBody }
    } catch (_: Exception) {
        errorBody.ifEmpty { "Error desconocido" }
    }
}
