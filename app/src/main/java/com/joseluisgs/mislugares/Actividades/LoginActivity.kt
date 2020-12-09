package com.joseluisgs.mislugares.Actividades

import Utilidades.Cifrador
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*

class LoginActivity : AppCompatActivity() {

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
    }

    private fun iniciarSesion() {
        if (comprobarFormulario()) {
            val pass = Cifrador.toHash(loginInputPass.text.toString())
            Log.i("Login", pass!!)
        }
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