package com.example.appchallengemeli.data.remote.api

import com.example.appchallengemeli.data.remote.dto.ItemDescriptionDto
import com.example.appchallengemeli.data.remote.dto.ItemDetailDto
import com.example.appchallengemeli.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MeliApi {

    @GET("sites/MLA/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): SearchResponseDto

    @GET("items/{item_id}")
    suspend fun getItemDetail(
        @Path("item_id") itemId: String
    ): ItemDetailDto

    @GET("items/{item_id}/description")
    suspend fun getItemDescription(
        @Path("item_id") itemId: String
    ): ItemDescriptionDto
}
