package com.joseluisgs.mislugares.UI.lugares

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Clase Detalle
 * @constructor
 */
class LugarDetalleFragment : Fragment() {
    private val MODO = Modo.INSERTAR
    private var lugarActual: Lugar? = null

    // Variables a usar y permisos del mapa
    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lugar_detalle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Lugares", "Creando Lugar Detalle")

        initIU();

    }

    /**
     * Iniciamos los elementos de la interfaz
     */
    private fun initIU() {
        // Inicializamos las cosas comunes y las específicass
        // Si es insertar
        if(this.MODO == Modo.INSERTAR) {
            initModoInsertar()
        }
    }

    /**
     * Crea todos los elementos en modo insertar
     */
    private fun initModoInsertar() {
        // Ocultamos o quitamos lo que no queremos ver en este modo
        detalleLugarTextVotos.visibility = View.GONE // View.INVISIBLE
        detalleLugarInputTipo.visibility = View.GONE
        detalleLugarEditFecha.visibility = View.GONE
        // Fecha
        val date = LocalDateTime.now()
        detalleLugarBotonFecha.text = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date)
        detalleLugarBotonFecha.setOnClickListener {  escogerFecha() }


    }

    /**
     * Inicia el DatePicker
     */
    private fun escogerFecha() {
        val date = LocalDateTime.now()
        //Abrimos el DataPickerDialog
        val datePickerDialog = DatePickerDialog(
            context!!,
            { _, mYear, mMonth, mDay ->
                detalleLugarBotonFecha.text = (mDay.toString() + "/" + (mMonth+1) + "/" + mYear)
            }, date.year, date.monthValue-1, date.dayOfMonth
        )
        datePickerDialog.show()
    }
}