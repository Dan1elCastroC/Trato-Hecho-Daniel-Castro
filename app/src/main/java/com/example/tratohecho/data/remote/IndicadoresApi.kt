package com.example.tratohecho.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface IndicadoresApi {
    @GET("api")
    suspend fun obtenerIndicadores(): Indicadores
}

object RetrofitInstance {
    val indicadoresApi: IndicadoresApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadoresApi::class.java)
    }
}
