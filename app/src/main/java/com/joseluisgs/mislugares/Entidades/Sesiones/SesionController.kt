package com.joseluisgs.mislugares.Entidades.Sesiones

import android.content.Context
import android.util.Log
import com.joseluisgs.mislugares.Entidades.Preferencias.PreferenciasController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import io.realm.Realm
import io.realm.kotlin.where

/**
* Controlador de Sesion
*/
object SesionController  {

    /**
     * Lee los datos de una sesión local
     * @param context Context
     * @return Usuario?
     */
    fun getLocal(context: Context): Usuario? {
        if(PreferenciasController.comprobarSesion(context)) {
            try {
                return PreferenciasController.leerSesion(context)
            } catch (ex: Exception) {
                Log.i("Sesion", "Error al leer sesion: " + ex.localizedMessage)
                return null
            }
        }
        return null
    }
}