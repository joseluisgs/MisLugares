package com.joseluisgs.mislugares.Preferencias

import Utilidades.Cifrador
import android.content.Context
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.R.drawable.user_avatar
import com.joseluisgs.mislugares.Usuarios.Usuario
import com.joseluisgs.mislugares.Usuarios.UsuarioController
import com.joseluisgs.mislugares.Utilidades.ImageBase64

/**
 * Clase para el manejo de preferencias
 */
object PreferenciasController {
    private var USER_ID: Long = 0L
    private lateinit var USER: Usuario

    /**
     * Comrpueba que existe una sesión abierta
     * @param context Context
     * @return Boolean
     */
    fun comprobarSesion(context: Context): Boolean {
        // Abrimos las preferencias en modo lectura
        val prefs = context.getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        USER_ID = prefs.getLong("USER_ID", 0L)
        return USER_ID != 0L
    }

    /**
     * Crea una sesión con el usuario por defecto
     * @param context Context
     */
    fun crearSesion(context: Context): Usuario {
        // Creamos un usuario por defecto para la BB.DD y la sesión
        // Esto lo creo porque no voy a tener registro si no no podría hacerlo así, debería registrar
        // De esta manera si saliese de la sesión siempre crearía el mismo usuario con distinto ID
        var usuario = Usuario(
            nombre = "José Luis González Sánchez",
            login = "joseluisgs",
            password = Cifrador.toHash("1234", "SHA-256")!!,
            avatar = ImageBase64.toBase64( BitmapFactory.decodeResource(context.resources, user_avatar))!!,
            correo  = "jlgs@cifpvirgendegracia.com",
            twitter = "https://twitter.com/joseluisgonsan",
            github = "https://github.com/joseluisgs"
        )
        // Lo insertamos en la Base de Datos
        UsuarioController.insert(usuario);
        // Consultamos su ID
        usuario = UsuarioController.selectByLogin(usuario.login)!!

        // Abrimos las preferemcias en modo escritura
        val prefs = context.getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong("USER_ID", usuario.id)
        // Escribimos el usuario como JSON
        editor.putString("USER", Gson().toJson(usuario))
        editor.commit()
        return usuario
    }

    /**
     * Leemos la sesion activa
     * @param context Context
     * @return Usuario
     */
    fun leerSesion(context: Context): Usuario {
        val prefs = context.getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        val usuario: Usuario = Gson().fromJson(prefs.getString("USER", ""), Usuario::class.java)
        return usuario
    }
}