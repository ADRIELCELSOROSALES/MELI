package com.example.appchallengemeli.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ItemDescriptionDto(
    @SerializedName("plain_text") val plainText: String?
)
