package com.joseluisgs.mislugares.UI.acerca_de

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.Utils
import kotlinx.android.synthetic.main.fragment_acerca_de.*

class AcercaDeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        initBotonesEventos()
    }

    /**
     * Inicia los eventos de botones y su funcionalidad
     */
    private fun initBotonesEventos() {
        // Correo electrónico
        acercaDeMail.setOnClickListener {
            Utils.mandarEMail(activity, para = "jlgs@cifpvirgendegracia.com", asunto ="Contacto Mis Lugares")
        }
        // Enlaces Web
        acercaDeGithub.setOnClickListener { Utils.abrirURL(activity!!, "https://github.com/joseluisgs") }
        acercaDeTwitter.setOnClickListener { Utils.abrirURL(activity!!, "https://twitter.com/joseluisgonsan") }
    }
}
