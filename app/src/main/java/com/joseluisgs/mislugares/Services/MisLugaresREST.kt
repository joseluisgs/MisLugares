package com.joseluisgs.mislugares.Services

import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaDTO
import com.joseluisgs.mislugares.Entidades.Lugares.LugarDTO
import com.joseluisgs.mislugares.Entidades.Sesiones.SesionDTO
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.*

/**
 * Métodos y llamadas para procesar las acciones sobre los EndPoints
 * de nuestra API RESR
 */
interface MisLugaresREST {
    // SESIONES
    // Obtener Sesion por ID de usuario
    @GET("sesiones/{id}")
    fun sesionGetById(@Path("id") id: String): Call<SesionDTO>
    // Actualiza la sesion del usuario
    @PUT("sesiones/{id}")
    fun sesionUpdate(@Path("id") id: String, @Body sesion: SesionDTO): Call<SesionDTO>
    // Elimina la sesión
    @DELETE("sesiones/{id}")
    fun sesionDelete(@Path("id") id: String): Call<SesionDTO>
    // Inserta una sesión
    @POST("sesiones/")
    fun sesionPost(@Body sesion: SesionDTO): Call<SesionDTO>

    // USUARIOS
    // Obtener Sesion por ID de usuario
    @GET("usuarios/{id}")
    fun usuarioGetById(@Path("id") id: String): Call<UsuarioDTO>

    // LUGARES
    // Obtiene todos los lugares
    @GET("lugares/")
    fun lugarGetAll(): Call<List<LugarDTO>>
    // Obtiene todos los lugares que tengan en su campo el userID que le paso
    @GET("lugares/")
    fun lugarGetAllByUserID(@Query("usuarioID") usuarioID: String): Call<List<LugarDTO>>
    // Actualiza un lugar
    @PUT("lugares/{id}")
    fun lugarUpdate(@Path("id") id: String, @Body lugarDTO: LugarDTO): Call<LugarDTO>
    // Inserta un lugar
    @POST("lugares/")
    fun lugarPost(@Body lugar: LugarDTO): Call<LugarDTO>
    // Elimina el lugar
    @DELETE("lugares/{id}")
    fun lugarDelete(@Path("id") id: String): Call<LugarDTO>

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

}