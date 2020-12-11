package com.joseluisgs.mislugares.Services

import com.joseluisgs.mislugares.Entidades.Sesiones.SesionDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.*

/**
 * Tiene los métodos y llamadas para procesar las acciones sobre los EndPoints
 */
interface MisLugaresREST {
    // Obtener Sesion por ID de usuario
    @GET("sesiones/{id}")
    fun sesionById(@Path("id") id: String): Call<SesionDTO>

//    // Obtener todos
//    // https://my-json-server.typicode.com/joseluisgs/APIRESTFake/users
//    @GET("users/")
//    fun findAll(): Call<List<UsuarioDTO>>
//
//    // Obtener por ID
//    // GET: https://my-json-server.typicode.com/joseluisgs/APIRESTFake/users/{id}
//    @GET("users/{id}")
//    fun findById(@Path("id") id: String): Call<UsuarioDTO>
//
//    // Crear un item
//    //POST: https://my-json-server.typicode.com/joseluisgs/APIRESTFake/users/
//    @POST("users/")
//    fun create(@Body user: UsuarioDTO): Call<UsuarioDTO>
//
//    // Elimina un item
//    // DELETE: https://my-json-server.typicode.com/joseluisgs/APIRESTFake/users/{id}
//    @DELETE("users/{id}")
//    fun delete(@Path("id") id: String): Call<UsuarioDTO>
//
//    // Actualiza un producto
//    // PUT: https://my-json-server.typicode.com/joseluisgs/APIRESTFake/users/{id}
//    @PUT("users/{id}")
//    fun update(@Path("id") id: String, @Body producto: UsuarioDTO): Call<UsuarioDTO>
}