package com.joseluisgs.mislugares.Actividades

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.CirculoTransformacion
import com.joseluisgs.mislugares.Utilidades.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private var permisos = false

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                R.id.nav_lugares, R.id.nav_mapa, R.id.nav_slideshow, R.id.nav_acerca_de
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Elementos propios de la interfaz y funcionalidad
        comprobarConexion()
        initIU()
    }

    private fun initIU() {
        // actualizamos el perfil con los datos de la sesion
        val navigationView : NavigationView  = findViewById(R.id.nav_view)
        val headerView : View = navigationView.getHeaderView(0)
        val navUsername : TextView = headerView.findViewById(R.id.navHeaderUserName)
        val navUserEmail : TextView = headerView.findViewById(R.id.navHeaderUserEmail)
        val navUserImage: ImageView = headerView.findViewById(R.id.navHeaderUserImage)
        navUsername.text = (this.application as MyApp).SESION_USUARIO.login
        navUserEmail.text = (this.application as MyApp).SESION_USUARIO.correo
        if((this.application as MyApp).SESION_USUARIO.avatar!=null){
            Picasso.get()
                .load(R.drawable.user_avatar) //Instanciamos un objeto de la clase (creada más abajo) para redondear la imagen
               .transform(CirculoTransformacion())
               .resize(150, 150)
                .into(navUserImage)
        }
    }

    private fun comprobarConexion() {
        // Comprobamos la red
        comprobarRed()
        comprobarGPS()
    }

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
                startActivity(intent);
            }
            snackbar.show()
        }
    }

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
                startActivity(intent);
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
}