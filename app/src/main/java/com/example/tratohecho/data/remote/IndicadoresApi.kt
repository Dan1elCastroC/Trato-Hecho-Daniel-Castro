package com.example.tratohecho.data.remote

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
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
            // mindicador.cl puede tardar ~40 s en responder; el valor por defecto (10 s) fallaba.
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .callTimeout(120, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadoresApi::class.java)
    }

    /**
     * URL del microservicio Spring Boot propio.
     * 10.0.2.2 = el PC visto desde el emulador. En un celular real: http://IP-DEL-PC:8080/
     */
    const val URL_MICROSERVICIO = "http://10.0.2.2:8080/"

    val tratoHechoApi: TratoHechoApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_MICROSERVICIO)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TratoHechoApi::class.java)
    }
}
