package com.hyunwookshin.dock2

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BearerInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val t = tokenProvider()
        val req = chain.request().newBuilder().apply {
            if (!t.isNullOrBlank()) addHeader("Authorization", "Bearer $t")
            addHeader("Content-Type", "application/json")
        }.build()
        return chain.proceed(req)
    }
}

object Net {
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun api(tokenProvider: () -> String?): Ec2Service {
        val client = OkHttpClient.Builder()
            .addInterceptor(BearerInterceptor(tokenProvider))
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://8qd0mfh8s6.execute-api.us-east-2.amazonaws.com/") // your stage base
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // or Moshi if you chose that stack
            .build()
            .create(Ec2Service::class.java)
    }
}
