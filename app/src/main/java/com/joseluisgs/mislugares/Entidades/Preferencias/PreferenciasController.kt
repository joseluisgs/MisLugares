package com.joseluisgs.mislugares.Entidades.Preferencias

import Utilidades.Cifrador
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.joseluisgs.mislugares.Entidades.Sesiones.Sesion
import com.joseluisgs.mislugares.R.drawable.user_avatar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioController
import com.joseluisgs.mislugares.Utilidades.ImageBase64

/**
 * Clase para el manejo de preferencias
 */
object PreferenciasController {
    private var USER_ID: String = ""
    private lateinit var USER: Usuario

    /**
     * Comrpueba que existe una sesión abierta
     * @param context Context
     * @return Boolean
     */
    fun comprobarSesion(context: Context): Boolean {
        // Abrimos las preferencias en modo lectura
        val prefs = context.getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        USER_ID = prefs.getString("USER_ID", "").toString()
        Log.i("Config", "Usuario ID: " + USER_ID)
        return USER_ID.isNotEmpty()
    }

    /**
     * Crea una sesión con el usuario por defecto
     * @param context Context
     */
    fun crearSesion(usuario: Usuario, context: Context) {
        // Abrimos las preferemcias en modo escritura
        val prefs = context.getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("USER_ID", usuario.id)
        // Escribimos el usuario como JSON
        editor.putString("USER", Gson().toJson(usuario))
        editor.apply()
    }

    /**
     * Leemos la sesion activa el usuario
     * @param context Context
     * @return Usuario
     */
    fun leerSesion(context: Context): Usuario{
        val prefs = context.getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        return Gson().fromJson(prefs.getString("USER", ""), Usuario::class.java)
    }
}