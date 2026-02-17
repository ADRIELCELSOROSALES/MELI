package com.example.appchallengemeli.data.remote.interceptor

import com.example.appchallengemeli.core.AppConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer ${AppConfig.ACCESS_TOKEN}")
            .build()
        return chain.proceed(request)
    }
}
