package com.example.appchallengemeli.core

/**
 * Centralized configuration for the app.
 * When the access token expires (every 6 hours), replace it here and rebuild.
 */
object AppConfig {
    const val BASE_URL = "https://api.mercadolibre.com/"
    const val ACCESS_TOKEN = "YOUR_ACCESS_TOKEN_HERE"

    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 15L
    const val DEFAULT_PAGE_LIMIT = 20
}
