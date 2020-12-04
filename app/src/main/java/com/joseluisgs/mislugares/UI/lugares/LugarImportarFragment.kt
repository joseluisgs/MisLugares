package com.joseluisgs.mislugares.UI.lugares

import Utilidades.Cifrador
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaController
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.CaptureActivity
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.fragment_importar_lugar.*
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*
import java.time.Instant
import java.util.*

class LugarImportarFragment: Fragment(), OnMapReadyCallback {
    private lateinit var LUGAR: Lugar
    private lateinit var mMap: GoogleMap
    private var posicion: LatLng? = null
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_importar_lugar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }
        initUI()
        scanQRCode()
        // Iniciamos la interfaz
    }

    private fun initUI() {
        importarFabAccion.setImageResource(R.drawable.ic_qr_code)
        importarFabAccion.backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.qrCodeColor)
        importarFabAccion.setOnClickListener { scanQRCode() }
        importarLugarTextNombre.visibility = View.INVISIBLE
        importarLugarTextTipo.visibility = View.INVISIBLE
        importarLugarTextFecha.visibility = View.INVISIBLE
        initMapa()
        mapFragment.view?.visibility = View.INVISIBLE

    }


    /**
     * Escanea el código
     */
    private fun scanQRCode() {
        val integrator = IntentIntegrator.forSupportFragment(this).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt(getString(R.string.escaneando_codigo))
        }
        integrator.initiateScan()
    }

    /**
     * Procesamos los resultados
     * @param requestCode Int
     * @param resultCode Int
     * @param data [ERROR : Intent]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_LONG).show()
            }
            else {
                try {
                    LUGAR = Gson().fromJson(result.contents, Lugar::class.java)
                    // Toast.makeText(context, "Recuperado: $LUGAR", Toast.LENGTH_LONG).show()
                    initUI()
                    initRespuesta()
                } catch (ex: Exception) {
                    Toast.makeText(context, "Error: El QR no es de un lugar válido", Toast.LENGTH_LONG).show()
                    // volver()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Inicia la UI
     */
    private fun initRespuesta() {
        importarLugarTextNombre.visibility = View.VISIBLE
        importarLugarTextTipo.visibility = View.VISIBLE
        importarLugarTextFecha.visibility = View.VISIBLE

        importarLugarTextNombre.text = LUGAR.nombre
        importarLugarTextTipo.text = LUGAR.tipo
        importarLugarTextFecha.text = LUGAR.fecha
        importarFabAccion.setImageResource(R.drawable.ic_importar)
        importarFabAccion.backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.importColor)
        importarFabAccion.setOnClickListener { importarLugar() }

        mapFragment.view?.visibility = View.VISIBLE
        initMapa()
        puntoEnMapa()

    }

    /**
     * Precondiciones de importar
     */
    private fun importarLugar() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_exit_app)
            .setTitle("¿Importar Lugar?")
            .setMessage("¿Desea importar este lugar?")
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which -> importar() }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    /**
     * Importa el lugar
     */
    private fun importar() {
        // Como sabemos que no tiene imagen le creamos una
        val imagen = AppCompatResources.getDrawable(context!!, R.drawable.ic_mapa_item_lista)?.toBitmap()
        val b64 = ImageBase64.toBase64(imagen!!)
        val fotografia = Fotografia(
            id = UUID.randomUUID().toString(),
            imagen = b64!!,
            hash = Cifrador.toHash(b64).toString(),
            time = Instant.now().toString(),
            usuarioID = (activity?.application as MyApp).SESION_USUARIO.toString()
        )
        FotografiaController.insert(fotografia)
        Log.i("Insertar", "Fotografía insertada con éxito con: " + fotografia.id)
        // Insertamos lugar
        val lugar = Lugar(
            id= UUID.randomUUID().toString(),
            nombre = LUGAR.nombre,
            tipo = LUGAR.tipo,
            fecha = LUGAR.fecha,
            latitud = LUGAR.latitud,
            longitud = LUGAR.longitud,
            imagenID = fotografia.id,
            favorito = false,
            votos = 0,
            time = Instant.now().toString(),
            usuarioID = (activity?.application as MyApp).SESION_USUARIO.toString()
        )
        LugarController.insert(lugar)
        Snackbar.make(view!!, "¡Lugar añadido con éxito!", Snackbar.LENGTH_LONG).show();
        Log.i("Insertar", "Lugar insertado con éxito con id" + LUGAR)
        initUI()
    }

    /**
     * Inicia el Mapa
     */
    private fun initMapa() {
        Log.i("Mapa", "Iniciando Mapa")
        mapFragment = (childFragmentManager
            .findFragmentById(R.id.importarLugarMapa) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)
    }

    /**
     * EL mapa está listo
     * @param googleMap GoogleMap
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        configurarIUMapa()
    }

    /**
     * Configuración por defecto del modo de mapa
     */
    private fun configurarIUMapa() {
        Log.i("Mapa", "Configurando IU Mapa")
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val uiSettings: UiSettings = mMap.uiSettings
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isTiltGesturesEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isMapToolbarEnabled = true
        mMap.setMinZoomPreference(12.0f)
    }

    /**
     * Muestra el punto en el mapa
     */
    fun puntoEnMapa() {
        posicion = LatLng(LUGAR.latitud.toDouble(), LUGAR.longitud.toDouble())
        mMap.addMarker(
            MarkerOptions() // Posición
                .position(posicion!!) // Título
                .title(LUGAR.nombre) // Subtitulo
                .snippet(LUGAR.tipo + " del " + LUGAR.fecha) // Color o tipo d icono
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion))
    }
}