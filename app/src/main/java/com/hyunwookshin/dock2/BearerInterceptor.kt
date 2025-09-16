package com.hyunwookshin.dock2

import okhttp3.Interceptor

class BearerInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = tokenProvider()
        require(!token.isNullOrBlank()) { "Not signed in" }  // ← hard fail
        val req = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(req)
    }
}