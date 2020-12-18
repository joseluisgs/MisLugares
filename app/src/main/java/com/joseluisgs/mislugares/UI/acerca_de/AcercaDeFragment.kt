package com.joseluisgs.mislugares.UI.acerca_de

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.Utils
import kotlinx.android.synthetic.main.fragment_acerca_de.*

class AcercaDeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acerca_de, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicialiamos la IU
        initUI()
    }

    /**
     * Inicializamos los elementos de la IU
     */
    private fun initUI() {
        iniciarAnimaciones()
        initBotonesEventos()
    }

    private fun iniciarAnimaciones() {
        val animacion1 = AnimationUtils.loadAnimation(context, R.anim.desplazamiento_arriba)
        val animacion2 = AnimationUtils.loadAnimation(context, R.anim.desplazamiento_abajo)
        val animacion3 = AnimationUtils.loadAnimation(context, R.anim.desplazamiento_lateral)

        acercaDeImage.animation = animacion1
        acercaDeAutor.animation = animacion2
        acercaDeCurso.animation = animacion2
        acercaDeInstituto.animation = animacion2
        acercaDeLugar.animation = animacion2
        acercaDeMail.animation = animacion3
        acercaDeTwitter.animation = animacion3
        acercaDeGithub.animation = animacion3
    }

    /**
     * Inicia los eventos de botones y su funcionalidad
     */
    private fun initBotonesEventos() {
        // Correo electrónico
        acercaDeMail.setOnClickListener { mandarMail() }
        // Enlaces Web
        acercaDeGithub.setOnClickListener { abrirWeb("https://github.com/joseluisgs") }
        acercaDeTwitter.setOnClickListener { abrirWeb("https://twitter.com/joseluisgonsan") }
    }

    /**
     * Abre una web
     * @param url String
     */
    private fun abrirWeb(url: String) {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_abrir_web)
            .setTitle(getString(R.string.dialogo_abrir_web_titulo))
            .setMessage(getString(R.string.dialogo_abrir_web_mensaje))
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which ->
                Utils.abrirURL(activity!!, url)
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    /**
     * Manda un mail
     */
    private fun mandarMail() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_mandar_mail)
            .setTitle(getString(R.string.contactar_email))
            .setMessage(getString(R.string.cantactar_email_mensaje))
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which ->
                Utils.mandarEMail(activity, para = "jlgs@cifpvirgendegracia.com", asunto = "Contacto Mis Lugares")
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }
}
