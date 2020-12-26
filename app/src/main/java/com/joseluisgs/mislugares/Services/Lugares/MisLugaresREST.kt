package com.joseluisgs.mislugares.Services.Lugares

import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaDTO
import com.joseluisgs.mislugares.Entidades.Lugares.LugarDTO
import retrofit2.Call
import retrofit2.http.*

/**
 * Métodos y llamadas para procesar las acciones sobre los EndPoints
 * de nuestra API RESR
 */
interface MisLugaresREST {

    // FOTOGRAFIAS
    // Obtener Fotografias por su id
    @GET("fotografias/{id}")
    fun fotografiaGetById(@Path("id") id: String): Call<FotografiaDTO>

    // Inserta una fotografia
    @POST("fotografias/")
    fun fotografiaPost(@Body fotografia: FotografiaDTO): Call<FotografiaDTO>

    // Elimina la fotografía
    @DELETE("fotografias/{id}")
    fun fotografiaDelete(@Path("id") id: String): Call<FotografiaDTO>

    // Actualiza una fotografia
    @PUT("fotografias/{id}")
    fun fotografiaUpdate(@Path("id") id: String, @Body fotografiaDTO: FotografiaDTO): Call<FotografiaDTO>

    // Obtiene todas las fotografías de un usuario
    @GET("fotografias/")
    fun fotografiaGetAllByUserID(@Query("usuarioID") usuarioID: String): Call<List<FotografiaDTO>>

}