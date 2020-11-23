package com.joseluisgs.mislugares.App

import android.app.Application
import android.content.Context
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration


class MyConfig : Application() {
    private val DATOS_BD = "MIS_LUGARES_BD"
    private val DATOS_BD_VERSION = 1L
    override fun onCreate() {
        super.onCreate()
        Log.i("Config", "Init Configuración")
        // Cargamos las preferencias o escribimos las prefernecias pordefector
        // Configuramos todo lo relacionado con REALM
        realmBD()
        preferenciasApp()
        Log.i("Config", "Fin Configuración")
    }

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

    private fun preferenciasApp() {
        // Vamos a simular que una vez que nos conectamos hemos metido al usuario en la BB.DD
        Log.i("Config", "Init Preferencias")
        val prefs = getSharedPreferences("MisLugares", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("USER_ID", "1")
        editor.putString("USER_LOGIN", "joseluisgs")
        editor.commit()
        Log.i("Config", "Fin Preferencias")
    }
}