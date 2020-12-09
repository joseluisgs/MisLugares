package com.joseluisgs.mislugares.Actividades

import Utilidades.Cifrador
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Sesiones.Sesion
import com.joseluisgs.mislugares.Entidades.Sesiones.SesionController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioController
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.activity_login.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


class LoginActivity : AppCompatActivity() {
    val MAX_TIME_SEG = 600 // Tiempo en segundos
    lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar))

        initUI()
    }

    private fun initUI() {
        // Datos para no meterlos
        loginInputLogin.setText("joseluisgs")
        loginInputPass.setText("1234")
        loginBoton.setOnClickListener{ iniciarSesion() }

        // SesionController.removeAll()
        // comprobamos si hay una sesión activa
        comprobarSesionActiva()
    }

    /**
     * Abre la sesión principal
     */
    private fun abrirPrincipal() {
        val main = Intent(this, MainActivity::class.java)
        startActivity(main)
        finish()
    }

    /**
     * Comprueba si hay una sesión activa
     */
    private fun comprobarSesionActiva(): Boolean {
        try {
            val sesion = SesionController.getFirst()
            // Log.i("Login", "SI hay sesion activa")
            // Obtengo el usuario de la sesion
            usuario = UsuarioController.selectById(sesion!!.usuarioID)!!
            // Log.i("Login", "Usuario: " + usuario.login)
            // Vemos si no ha caducado
            val now = Instant.now()
            Log.i("Login", "now: ${now.atZone(ZoneId.systemDefault())}")
            val time = Instant.parse(sesion.time)
            Log.i("Login", "time: ${time.atZone(ZoneId.systemDefault())}")
            val seg = ChronoUnit.SECONDS.between(time, now)
            // Log.i("Login", "Aqui!")
            if (seg>=MAX_TIME_SEG) {
                Log.i("Login", "Sesion ha Caducado")
                return false
            } else {
                // Almacenamos la sesion activa
                Log.i("Login", "Sesion activa, entramos")
                (this.application as MyApp).SESION_USUARIO = usuario
                abrirPrincipal()
                return true
            }
            
        } catch (ex: Exception) {
            Log.i("Login", "NO hay sesion activa o no existe sesiones")
            Log.i("Login", "Error: " + ex.localizedMessage)
            return false
        }
    }

    /**
     * Inicia una sesion
     * @return Boolean
     */
    private fun iniciarSesion(): Boolean {
        if (comprobarFormulario()) {
            val pass = Cifrador.toHash(loginInputPass.text.toString()).toString()
            // buscamos el usuario
            try {
                usuario = UsuarioController.selectByLogin(loginInputLogin.text.toString())!!
                //Log.i("Login", usuario.password)
                // Log.i("Login", pass)
                if( usuario.password == pass ) {
                    almacenarSesion(usuario)
                } else {
                    mensajeError()
                    return false
                }
            } catch (ex: Exception) {
                mensajeError()
                return false
            }
        }
        return false
    }

    /**
     * Almacenamos la sesion y pasamos
     * @param usuario Usuario
     */
    private fun almacenarSesion(usuario: Usuario) {
        // Creamos la sesion
        val sesion = Sesion(
            usuarioID = usuario.id,
            time = Instant.now().toString(),
            token = UUID.randomUUID().toString()
        )
        try {
            // Borramos el anterior si lo hay
            SesionController.deleteByID(usuario.id)
            SesionController.insert(sesion)
            // Cargamos el usuario en la sesion
            (this.application as MyApp).SESION_USUARIO = usuario
            // abrimos la siguiente
            Log.i("Login", "usuario y passs correctos")
            abrirPrincipal()
        } catch (ex: Exception) {
            Log.i("Login", "Error al crear la sesion")
            Log.i("Login", "Error: " + ex.localizedMessage)
        }

    }

    /**
     * Mensaje genérico de error
     */
    fun mensajeError() {
        Log.i("Login", "usuario o pas incorrectos")
        Snackbar.make(
            findViewById(android.R.id.content),
            "Usuario o Contraseña incorrectos",
            Snackbar.LENGTH_LONG
        ).show()
    }

    /**
     * Comprueba que no haya campos nulos
     * @return Boolean
     */
    private fun comprobarFormulario(): Boolean {
        var sal = true
        if (loginInputLogin.text!!.isEmpty()) {
            loginInputLogin.error = "El nombre de usuario no puede estar en blanco"
            sal = false
        }

        if (loginInputPass.text!!.isEmpty()) {
            loginInputPass.error = "La contraseña no puede estar en blanco"
            sal = false
        }
        return sal
    }
}