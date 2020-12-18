package com.joseluisgs.mislugares.Services.Tiempo

import com.joseluisgs.mislugares.Entidades.Tiempo.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Métodos y llamadas para procesar las acciones sobre los EndPoints
 * de nuestra API REST
 */
interface ElTiempoREST {
    @GET("data/2.5/weather?")
    fun getCurrentWeatherData(
        @Query("lat") lat: String, @Query("lon") lon: String,
        @Query("APPID") app_id: String, @Query("units") units: String,
        @Query("lang") lang: String,
    ): Call<WeatherResponse>
}