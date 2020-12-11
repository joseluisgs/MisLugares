package com.joseluisgs.mislugares.Actividades

import Utilidades.Cifrador
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Sesiones.Sesion
import com.joseluisgs.mislugares.Entidades.Sesiones.SesionController
import com.joseluisgs.mislugares.Entidades.Sesiones.SesionDTO
import com.joseluisgs.mislugares.Entidades.Sesiones.SesionMapper
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioController
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.MisLugaresAPI
import com.joseluisgs.mislugares.Utilidades.Utils
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


class LoginActivity : AppCompatActivity() {
    private val MAX_TIME_SEG = 600 // Tiempo en segundos
    private lateinit var usuario: Usuario
    private lateinit var sesionRemota: Sesion

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

        if (Utils.isNetworkAvailable(applicationContext)) {
            procesarSesiones()
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
    private fun procesarSesiones() {
        try {
            // Comprobamos si hay una sesion Local, viendo el usuario almacenado
            usuario = SesionController.getLocal(this)!!
            // Si tenemos sesion activa comprobamos lso datos respecto a la remota
            if(usuario!=null) {
                comprobarSesionRemota(usuario)
            }


//            // Log.i("Login", "Aqui!")
//            if (seg>=MAX_TIME_SEG) {
//                Log.i("Login", "Sesion ha Caducado")
//                return false
//            } else {
//                // Almacenamos la sesion activa
//                Log.i("Login", "Sesion activa, entramos")
//                (this.application as MyApp).SESION_USUARIO = usuario
//                abrirPrincipal()
//                return true
//            }
            
        } catch (ex: Exception) {
            Log.i("Login", "NO hay sesion activa o no existe sesiones")
            Log.i("Login", "Error: " + ex.localizedMessage)
        }
    }

    /**
     * Comprueba la sesión remota
     * @param usuario Usuario
     */
    private fun comprobarSesionRemota(usuario: Usuario) {
        val clientREST = MisLugaresAPI.service
        val call: Call<SesionDTO> = clientREST.sesionById(usuario.id)
        call.enqueue((object : Callback<SesionDTO> {
            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
                // Si ok
                if (response.isSuccessful) {
                   Log.i("REST", "SesionByID ok")
                    var remoteSesion = SesionMapper.fromDTO(response.body() as SesionDTO)
                    sesionRemota = Sesion()
                    sesionRemota.fromSesion(remoteSesion)
                    // Si la obtiene comparamos
                    compararSesiones()
                } else {
                    // Si falla crea una sesión nueva
                    Log.i("REST", "Error: SesionByID isSuccessful")
                }
            }
            override fun onFailure(call: Call<SesionDTO>, t: Throwable) {
                Toast.makeText(applicationContext, "Error al acceder al servicio: " + t.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }))
    }

    /**
     * Compara las sesiones
     */
    private fun compararSesiones() {
        val now = Instant.now()
        Log.i("Login", "now: ${now.atZone(ZoneId.systemDefault())}")
        val time = Instant.parse(sesionRemota.time)
        Log.i("Login", "time: ${time.atZone(ZoneId.systemDefault())}")
        val seg = ChronoUnit.SECONDS.between(time, now)
        if (seg>=MAX_TIME_SEG) {
              Log.i("Login", "Sesion ha Caducado")
        } else {
            // Almacenamos la sesion activa
            Log.i("Login", "Sesion activa, entramos")
            (this.application as MyApp).SESION_USUARIO = usuario
            abrirPrincipal()
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