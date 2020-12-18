package com.joseluisgs.mislugares.Services.Tiempo

/**
 * Punto de acceso para entrar a la API
 */
object ElTiempoAPI {
    private const val API_URL = "http://api.openweathermap.org/"
    val API_KEY: String = "d1da41b678103181ec84cbc640adb315"
    val UNITS: String = "metric"
    val LANG: String = "es"
    val service: ElTiempoREST
        get() = ElTiempoClient.getClient(API_URL)!!.create(ElTiempoREST::class.java)
}