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
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioDTO
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioMapper
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
import com.joseluisgs.mislugares.Utilidades.Utils
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


class LoginActivity : AppCompatActivity() {
    private val MAX_TIME_SEG = 600 // Tiempo en segundos
    private lateinit var usuario: Usuario
    private lateinit var sesionRemota: Sesion
    private var existeSesion = false

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
        loginBoton.setOnClickListener { iniciarSesion() }

        // primero comprobamos que tengamos conexión
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
            comprobarSesionRemota(usuario)
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
        val call: Call<SesionDTO> = clientREST.sesionGetById(usuario.id)
        call.enqueue((object : Callback<SesionDTO> {

            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "SesionGetByID ok")
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
                Toast.makeText(applicationContext,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
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
        if (seg <= MAX_TIME_SEG) {
            Log.i("Login", "Sesion activa, entramos")
            (this.application as MyApp).SESION_USUARIO = usuario
            // Actualizamos la sesión su fecha
            actualizarSesion()
            abrirPrincipal()
        } else {
            existeSesion = true // Existe y ha caducado, para borrarla
            Log.i("Login", "Sesión ha caducado")
        }
    }

    /**
     * Actualiza la sesión remota
     */
    private fun actualizarSesion() {
        // Cogemos y actualizamos el tiempo
        sesionRemota.time = Instant.now().toString()
        val sesionDTO = SesionMapper.toDTO(sesionRemota)

        val clientREST = MisLugaresAPI.service
        val call: Call<SesionDTO> = clientREST.sesionUpdate(sesionRemota.usuarioID, sesionDTO)
        call.enqueue((object : Callback<SesionDTO> {

            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "SesionUpdate ok")
                } else {
                    Log.i("REST", "Error: SesionUpdate isSuccessful")
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
     * Inicia una sesion
     * @return Boolean
     */
    private fun iniciarSesion() {
        if (comprobarFormulario()) {
            val pass = Cifrador.toHash(loginInputPass.text.toString()).toString()
            val clientREST = MisLugaresAPI.service
            val call: Call<UsuarioDTO> = clientREST.usuarioGetById(usuario.id)
            call.enqueue((object : Callback<UsuarioDTO> {

                override fun onResponse(call: Call<UsuarioDTO>, response: Response<UsuarioDTO>) {
                    if (response.isSuccessful) {
                        Log.i("REST", "UsuarioGetByID ok")
                        val usuario = UsuarioMapper.fromDTO(response.body() as UsuarioDTO)
                        // Si la obtiene comparamos
                        if (usuario.password == pass) {
                            almacenarSesion()
                        } else {
                            mensajeError()
                            return
                        }
                    } else {
                        // Si falla crea una sesión nueva
                        Log.i("REST", "Error: UsuarioByID isSuccessful")
                    }
                }

                override fun onFailure(call: Call<UsuarioDTO>, t: Throwable) {
                    Toast.makeText(applicationContext,
                        "Error al acceder al servicio: " + t.localizedMessage,
                        Toast.LENGTH_LONG)
                        .show()
                }
            }))
        }
    }


    /**
     * Almacenamos la sesion y pasamos
     * @param usuario Usuario
     */
    private fun almacenarSesion() {
        // Creamos la sesion
        if (existeSesion) {
            eliminarSesionRemota()
        }
        // Creamos la sesion
        // Esto no se haría aquí si no lo haría el servidor pasándole el usuario y te devolvería el token
        // Pero nuesta API REST es simulada
        val sesion = Sesion(
            usuarioID = usuario.id,
            time = Instant.now().toString(),
            token = UUID.randomUUID().toString()
        )
        // Creamos la sesión remota
        val clientREST = MisLugaresAPI.service
        val call: Call<SesionDTO> = clientREST.sesionPost(SesionMapper.toDTO(sesion))
        call.enqueue((object : Callback<SesionDTO> {

            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "sesionPost ok")
                    (application as MyApp).SESION_USUARIO = usuario
                    abrirPrincipal()
                } else {
                    Log.i("REST", "Error sesionPost isSeccesful")
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
     * Elimina la sesión remota
     */
    private fun eliminarSesionRemota() {
        val clientREST = MisLugaresAPI.service
        val call: Call<SesionDTO> = clientREST.sesionDelete(usuario.id)
        call.enqueue((object : Callback<SesionDTO> {

            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "sesionDelete ok")
                    existeSesion = false
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