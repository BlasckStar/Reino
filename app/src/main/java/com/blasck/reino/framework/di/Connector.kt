package com.blasck.reino.framework.di

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Connector {
    private const val BASE_URL = "https://cad68b5fdec6a35deaba.free.beeceptor.com/"

    fun provideRetrofit() =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}