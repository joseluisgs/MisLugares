package com.joseluisgs.mislugares.Services.Tiempo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Devuelve la interfaz inicializada del cliente
 */
object ElTiempoClient {
    private var retrofit: Retrofit? = null
    fun getClient(url: String): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}