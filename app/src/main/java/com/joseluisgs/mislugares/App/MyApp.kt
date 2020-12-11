package com.joseluisgs.mislugares.App

import android.Manifest
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.joseluisgs.mislugares.Entidades.Preferencias.PreferenciasController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.realm.Realm
import io.realm.RealmConfiguration


class MyApp : Application() {
    // Propiedades, getters and setters (visibilidad) globales
    lateinit var SESION_USUARIO: Usuario
    var APP_PERMISOS = false
        private set

        // private set
    private val BD_NOMBRE = "MIS_LUGARES_BD"
    private val BD_VERSION = 1L



    override fun onCreate() {
        super.onCreate()
        Log.i("Config", "Init Configuración")
        initRealmBD()
        Log.i("Config", "Fin Configuración")
    }


    /**
     * Inicia Realm
     */
    private fun initRealmBD() {
        Log.i("Config", "Init Realm")
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder()
            .name(BD_NOMBRE)
            .schemaVersion(BD_VERSION) // Versión de esquema estamos trabajando, si lo cambiamos, debemos incrementar
            .deleteRealmIfMigrationNeeded() // Podemos borrar los datos que ya haya si cambiamos el esquema,
            .build()
        Realm.setDefaultConfiguration(config)
        Log.i("Config", "Fin Realm")
    }

    /**
     * Comprobamos los permisos de la aplicación
     */
     fun initPermisos() {
        Log.i("Config", "Init Permisos")
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withContext(this)
            // Lista de permisos a comprobar
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
            )
            // Listener a ejecutar
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // ccomprbamos si tenemos los permisos de todos ellos
                    if (report.areAllPermissionsGranted()) {
                        APP_PERMISOS = true
                        Toast.makeText(applicationContext, "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // abrimos un diálogo a los permisos
                        //openSettingsDialog();
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { Toast.makeText(applicationContext, "¡Existe errores! ", Toast.LENGTH_SHORT).show() }
            .onSameThread()
            .check()
        Log.i("Config", "Fin Permisos")
    }
}
