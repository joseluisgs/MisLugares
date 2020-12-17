package com.joseluisgs.mislugares.Services.Lugares

/**
 * Punto de acceso para entrar a la API
 */
object MisLugaresAPI {
    // private const val API_URL = "https://my-json-server.typicode.com/joseluisgs/APIRESTFake/"

    // Si estamos con un servidor propio en el ordenador, desde el emulador la ip es o conodemos su ip
   //  private const val server: String = "10.0.2.2" --> IP del Ordenador desde el emulador
    private const val server = "192.168.1.59"

    // Puerto del microservicio
    private const val port = "6969"
    private const val API_URL = "http://$server:$port/"

    // Constructor del servicio con los elementos de la interfaz
    val service: MisLugaresREST
        get() = MisLugaresClient.getClient(API_URL)!!.create(MisLugaresREST::class.java)
}