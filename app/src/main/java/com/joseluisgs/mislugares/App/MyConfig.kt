package com.joseluisgs.mislugares.App

import Utilidades.Cifrador
import android.app.Application
import android.content.Context
import android.util.Log
import com.joseluisgs.mislugares.Preferencias.PreferenciasController
import com.joseluisgs.mislugares.Usuarios.Usuario
import com.joseluisgs.mislugares.Usuarios.UsuarioController
import io.realm.Realm
import io.realm.RealmConfiguration


class MyConfig : Application() {
    private lateinit var usuarioActivo: Usuario
    private val DATOS_BD = "MIS_LUGARES_BD"
    private val DATOS_BD_VERSION = 1L
    override fun onCreate() {
        super.onCreate()
        Log.i("Config", "Init Configuración")
        // Cargamos las preferencias o escribimos las preferencias por defecto
        // Configuramos todo lo relacionado con REALM
        realmBD()
        preferenciasApp()
        Log.i("Config", "Fin Configuración")
    }

    /**
     * Inicia Realm
     */
    private fun realmBD() {
        Log.i("Config", "Init Realm")
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder()
            .name(DATOS_BD)
            .schemaVersion(DATOS_BD_VERSION) // Versión de esquema estamos trabajando, si lo cambiamos, debemos incrementar
            .deleteRealmIfMigrationNeeded() // Podemos borrar los datos que ya haya si cambiamos el esquema,
            .build()
        Realm.setDefaultConfiguration(config)
        Log.i("Config", "Fin Realm")
    }

    /**
     * Inicia algunas preferencias por defecto
     */
    private fun preferenciasApp() {
        // Vamos a simular que una vez que nos conectamos hemos metido al usuario en la BB.DD
        Log.i("Config", "Init Preferencias")
        val prefs = getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        // Comprobamos si hay sesion, es decir, si es != 0
        if(PreferenciasController.comprobarSesion(applicationContext)) {
            Log.i("Config", "Sí existe Sesión de usuario")
            usuarioActivo = PreferenciasController.leerSesion(applicationContext)
        } else {
            Log.i("Config", "No existe Sesión de usuario")
            usuarioActivo = PreferenciasController.crearSesion(applicationContext)
        }
        Log.i("Config", "Usuario activo Login: ${usuarioActivo.login} con Correo: ${usuarioActivo.correo}")
        Log.i("Config", "Fin Preferencias")
    }
}
