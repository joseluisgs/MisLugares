package com.joseluisgs.mislugares

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.joseluisgs.mislugares.Actividades.MainActivity

class SplashActivityActivity : AppCompatActivity() {
    private val TIME: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultamos los elementos que no queremos que salgan
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen)
        //crea un intent para ir al activity main
        val main = Intent(this, MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            run {
                startActivity(main)
                finish()
            }
        }, this.TIME)
    }
}