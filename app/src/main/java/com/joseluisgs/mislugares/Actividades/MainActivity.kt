package com.joseluisgs.mislugares.Actividades

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Sesiones.SesionDTO
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
import com.joseluisgs.mislugares.Utilidades.CirculoTransformacion
import com.joseluisgs.mislugares.Utilidades.Fotos
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import com.joseluisgs.mislugares.Utilidades.Utils
import com.squareup.picasso.Picasso
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    // Obtenemos el usuario de la sesión
    private lateinit var USER: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Limpiamos la cache y temporales.
        limpiarBasura()
        // elementos de la interfaz propios
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Identificamos los elementos para navegar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_lugares, R.id.nav_mapa, R.id.nav_importar_lugar,
                R.id.nav_acerca_de, R.id.nav_brujula, R.id.nav_linterna, R.id.nav_backup, R.id.nav_tiempo
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Elementos propios de la interfaz y funcionalidad
        initPermisos()
        leerSesion()
        comprobarConexion()
        initIU()
    }

    /**
     * Inicia/ Comprueba los permisos de la App
     */
    private fun initPermisos() {
        if (!(this.application as MyApp).APP_PERMISOS)
            (this.application as MyApp).initPermisos()
    }

    /**
     * Lee la sesión o usuario conectado
     */
    private fun leerSesion() {
        USER = (this.application as MyApp).SESION_USUARIO
    }

    /**
     * Inicia la interfaz de usuario
     */
    private fun initIU() {
        mostrarDatosUsuarioMenu()
    }


    /**
     * Carga los datos del usuario
     */
    private fun mostrarDatosUsuarioMenu() {
        // actualizamos el perfil con los datos de la sesion
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.navHeaderUserName)
        val navUserEmail: TextView = headerView.findViewById(R.id.navHeaderUserEmail)
        val navUserImage: ImageView = headerView.findViewById(R.id.navHeaderUserImage)
        navUsername.text = USER.login
        navUserEmail.text = USER.correo
        // Cargo la imagen como temporal
        val avatar = File(
            ImageBase64.fromTempUri(
                ImageBase64.toBitmap(USER.avatar)!!,
                applicationContext,
            ).path!!
        )
        Picasso.get()
            // .load(R.drawable.user_avatar)
            .load(avatar)
            .transform(CirculoTransformacion())
            .resize(130, 130)
            .into(navUserImage)
        // Elimino la imagen temporal
        // ImageBase64.removeTempFile(avatar)
        // Evento de salir
        navUserImage.setOnClickListener { salirSesion() }
    }

    /**
     * Sale de la sesion
     */
    private fun salirSesion() {
        Log.i("Salir", "Saliendo...")
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_exit_app)
            .setTitle("Cerrar sesión actual")
            .setMessage("¿Desea salir de la sesión actual?")
            .setPositiveButton(getString(R.string.si)) { dialog, which -> cerrarSesion() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    /**
     * Cerra la sesión Actual
     */
    private fun cerrarSesion() {
        // Borramos la sesión asociada
        val clientREST = MisLugaresAPI.service
        val call: Call<SesionDTO> = clientREST.sesionDelete(USER.id)
        call.enqueue((object : Callback<SesionDTO> {

            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "sesionDelete ok")
                    // Y vamos a login
                    val login = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(login)
                    finish()
                } else {
                    Log.i("REST", "Error: SesionDelete isSuccessful")
                }
            }

            override fun onFailure(call: Call<SesionDTO>, t: Throwable) {
                Toast.makeText(applicationContext,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))
    }

    /**
     * Comprueba que exista las conexiones para funcionar
     */
    private fun comprobarConexion() {
        // Comprobamos la red
        comprobarRed()
        comprobarGPS()
    }

    /**
     * Comprueba que existe GPS si no llama a activarlo
     */
    private fun comprobarGPS() {
        if (Utils.isGPSAvaliable(applicationContext)) {
            Toast.makeText(applicationContext, "Existe conexión a GPS", Toast.LENGTH_SHORT)
                .show()
        } else {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "Es necesaria una conexión a GPS",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setActionTextColor(getColor(R.color.colorAccent))
            snackbar.setAction("Conectar") {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            snackbar.show()
        }
    }

    /**
     * Comprueba que haya red, si no llama a activarlo
     */
    private fun comprobarRed() {
        if (Utils.isNetworkAvailable(applicationContext)) {
            Toast.makeText(applicationContext, "Existe conexión a internet", Toast.LENGTH_SHORT)
                .show()
        } else {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "Es necesaria una conexión a internet",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setActionTextColor(getColor(R.color.colorAccent))
            snackbar.setAction("Conectar") {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            }
            snackbar.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Al destruirse nuestra actividad o APP
     */
    override fun onDestroy() {
        super.onDestroy()
        // Cerramos REALM
        Realm.getDefaultInstance().close() // limpiamos realm
        limpiarBasura() // Por si acaso
        Log.i("Destroy", "Ejecutando OnDestroy")
    }

    /**
     * Limpia nuestros ficheros temporales
     */
    fun limpiarBasura() {
        Utils.deleteCache(this)
        Fotos.deleteFotoDir(this)
        Log.i("Basura", "Limpiando Basura")
    }

    /**
     * Quitamos fragment apilados, y si no hay salimos
     */
    override fun onBackPressed() {
        try {
            if (supportFragmentManager.backStackEntryCount > 0)
                supportFragmentManager.popBackStackImmediate()
            else
                confirmarSalir()
        } catch (ex: Exception) {
            confirmarSalir()
        }
    }

    /**
     * Mensaje para confirmar para salir
     */
    fun confirmarSalir() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_exit_app)
            .setTitle(getString(R.string.cerrar_app))
            .setMessage(getString(R.string.mensaje_cerrar))
            .setPositiveButton(getString(R.string.si)) { dialog, which -> finish() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}