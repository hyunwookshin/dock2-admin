package com.hyunwookshin.dock2

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Net {
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun api(tokenProvider: () -> String?): Ec2Service {
        val client = OkHttpClient.Builder()
            .addInterceptor(BearerInterceptor(tokenProvider))
            .addInterceptor(UnauthorizedInterceptor())
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://8qd0mfh8s6.execute-api.us-east-2.amazonaws.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Ec2Service::class.java)
    }
}

