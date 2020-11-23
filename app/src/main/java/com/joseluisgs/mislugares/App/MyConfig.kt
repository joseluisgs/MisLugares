package com.joseluisgs.mislugares.App

import android.app.Application
import android.content.Context
import android.util.Log
import com.joseluisgs.mislugares.Usuarios.Usuario
import com.joseluisgs.mislugares.Usuarios.UsuarioController
import io.realm.Realm
import io.realm.RealmConfiguration


class MyConfig : Application() {
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
        // Leemos las preferencias a ver si hay un usuario
        val USER_ID = prefs.getLong("USER_ID", 0L);
        val USER_LOGIN = prefs.getString("USER_LOGIN", "-");
        // Si hay usuario, cargamos su login y datos tras buscarlos en REALM y escribimos las preferencias
        var usuario: Usuario
        if(USER_ID != 0L) {
            Log.i("Config", "Sí existe usuario")
            // Podríamos consultar todos sus datos si qusiésemos
            // usuario = UsuarioController.selectById(USER_ID)!!
            // Escribiriamos o leeriamos las preferencias
            Log.i("Config", USER_LOGIN!!)
            // Si no hay deberíamos ir a Login, etc...
        } else {
            // Si no lo hay, lo creamos, lo insertamos y escribimos las preferencias
            Log.i("Config", "No existe usuario")
             usuario = Usuario(
                nombre = "Jose Luis",
                login = "joseluisgs",
                password = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4",
                avatar = "https://avatars0.githubusercontent.com/u/47913953?s=460&u=225a157fde1cb059c0541fd76f8230682b5cf130&v=4",
                correo = "jlgs@cifpvirgendegracia.com",
                twitter = "https://twitter.com/joseluisgonsan",
                github = "https://github.com/joseluisgs"
            )
            // Lo insertamos
            UsuarioController.insert(usuario);
            // Consultamos su ID
            usuario = UsuarioController.selectByLogin(usuario.login)!!
            val editor = prefs.edit()
            editor.putLong("USER_ID", usuario.id)
            editor.putString("USER_LOGIN", usuario.login)
            // Podríamos meter todos los datos que quisésemos
            editor.commit()
            Log.i("Config", usuario.login)
        }

        Log.i("Config", "Fin Preferencias")
    }
}
