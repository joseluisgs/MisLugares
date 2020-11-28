package com.joseluisgs.mislugares.UI.lugares

import android.app.DatePickerDialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Clase Detalle
 * @constructor
 */
class LugarDetalleFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var USUARIO: Usuario
    private var PERMISOS: Boolean = false
    private val MODO = Modo.INSERTAR

    // Variables a usar y permisos del mapa
    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 1
    private var mPosicion: FusedLocationProviderClient? = null
    private var lugar: Lugar? = null
    private var marcadorTouch: Marker? = null
    private var localizacion: Location? = null
    private var posicion: LatLng? = null


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

        initIU()

    }

    /**
     * Iniciamos los elementos de la interfaz
     */
    private fun initIU() {
        // Inicializamos las cosas comunes y las específicass
        initPermisos()
        initUsuario()
        // Si es insertar
        if (this.MODO == Modo.INSERTAR) {
            initModoInsertar()
        }
        leerPoscionGPSActual()
        initMapa()
    }

    /**
     * Lee el usuario
     */
    private fun initUsuario() {
        this.USUARIO = (activity?.application as MyApp).SESION_USUARIO
    }

    /**
     * Lee los permisos
     */
    private fun initPermisos() {
        this.PERMISOS = (activity?.application as MyApp).APP_PERMISOS
    }

    /**
     * Crea todos los elementos en modo insertar
     */
    private fun initModoInsertar() {
        // Ocultamos o quitamos lo que no queremos ver en este modo
        detalleLugarTextVotos.visibility = View.GONE // View.INVISIBLE
        detalleLugarInputTipo.visibility = View.GONE
        detalleLugarEditFecha.visibility = View.GONE
        detalleLugarInputNombre.setText("Tu Lugar Ahora")
        // Fecha
        val date = LocalDateTime.now()
        detalleLugarBotonFecha.text = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date)
        detalleLugarBotonFecha.setOnClickListener { escogerFecha() }
        detalleLugarFabAccion.setOnClickListener { insertarLugar() }

    }

    /**
     * Inserta un lugar
     */
    private fun insertarLugar() {
        if (comprobarFormulario()) {
            lugar = Lugar(
                nombre = detalleLugarInputNombre.text.toString(),
                fecha =  detalleLugarBotonFecha.text.toString(),
                tipo = (detalleLugarSpinnerTipo.selectedItem as String),
                latitud = posicion?.latitude.toString(),
                longitud = posicion?.latitude.toString(),
                // imagen = ImageBase64.toBase64(detalleLugarImagen.im)!!
                usuarioID = USUARIO.id

            )
            Snackbar.make(view!!, "¡Lugar añadido con éxito!", Snackbar.LENGTH_LONG).show();
            Log.i("Insertar", lugar.toString())
        }
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
                detalleLugarBotonFecha.text = (mDay.toString() + "/" + (mMonth + 1) + "/" + mYear)
            }, date.year, date.monthValue - 1, date.dayOfMonth
        )
        datePickerDialog.show()
    }

    /**
     * Comprueba que no haya campos nulos
     * @return Boolean
     */
    private fun comprobarFormulario(): Boolean {
        var sal = true
        if (detalleLugarInputNombre.text?.isEmpty()!!) {
            detalleLugarInputNombre.error = "El nombre no puede ser vacío"
            sal = false
        }
        return sal
    }

    /**
     * Leemos la posición actual del GPS
     */
    private fun leerPoscionGPSActual() {
        mPosicion = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    private fun initMapa() {
        Log.i("Mapa", "Iniciando Mapa")
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.detalleLugarMapa) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        configurarIUMapa()
        modoMapa()
    }

    /**
     * Configuración por defecto del modo de mapa
     */
    private fun configurarIUMapa() {
        Log.i("Mapa", "Configurando IU Mapa")
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val uiSettings: UiSettings = mMap.uiSettings
        // Activamos los gestos
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isTiltGesturesEnabled = true
        // Activamos la brújula
        uiSettings.isCompassEnabled = true
        // Activamos los controles de zoom
        uiSettings.isZoomControlsEnabled = true
        // Activamos la barra de herramientas
        uiSettings.isMapToolbarEnabled = true
        // Hacemos el zoom por defecto mínimo
        mMap.setMinZoomPreference(12.0f)
        mMap.setOnMarkerClickListener(this)
    }

    /**
     * Actualiza la interfaz del mapa según el modo
     */
    private fun modoMapa() {
        Log.i("Mapa", "Configurando Modo Mapa")
        when (this.MODO) {
            Modo.INSERTAR -> mapaInsertar()
            // VISUALIZAR -> mapaVisualizar()
            // ELIMINAR -> mapaVisualizar()
            // ACTUALIZAR -> mapaActualizar()
            else -> {
            }
        }
    }

    /**
     * Modo insertar del mapa
     */
    private fun mapaInsertar() {
        Log.i("Mapa", "Configurando Modo Insertar")
        if (this.PERMISOS) {
            mMap.isMyLocationEnabled = true
        }
        activarEventosMarcadores()
        obtenerPosicion()
    }

    /**
     * Activa los eventos de los marcadores
     */
    private fun activarEventosMarcadores() {
        mMap.setOnMapClickListener { point -> // Creamos el marcador
            // Borramos el marcador Touch si está puesto
            marcadorTouch?.remove()
            marcadorTouch = mMap.addMarker(
                MarkerOptions() // Posición
                    .position(point) // Título
                    .title("Tu posición") // Subtitulo
                    .snippet(detalleLugarInputNombre.text.toString()) // Color o tipo d icono
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point))
            posicion = point
        }
    }

    /**
     * Obtiene la posición
     */
    private fun obtenerPosicion() {
        Log.i("Mapa", "Opteniendo posición")
        try {
            if (this.PERMISOS) {
                // Lo lanzamos como tarea concurrente
                val local: Task<Location> = mPosicion!!.lastLocation
                local.addOnCompleteListener(
                    activity!!
                ) { task ->
                    if (task.isSuccessful) {
                        // Actualizamos la última posición conocida
                        localizacion = task.result
                        posicion = LatLng(
                            localizacion!!.latitude,
                            localizacion!!.longitude
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));
                    } else {
                        Snackbar.make(view!!, "No se ha encontrado su posoción actual", Snackbar.LENGTH_LONG).show();
                        Log.d("GPS", "No se encuetra la última posición.")
                        Log.e("GPS", "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
        }
    }

    /**
     * Evento al pulsar el marcador
     * @param marker Marker
     * @return Boolean
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        Log.i("Mapa", marker.toString())
//        Toast.makeText(
//                context, marker.title.toString() +
//                        " Mal sitio para ir.",
//                Toast.LENGTH_SHORT
//            ).show()
        return false
    }
}