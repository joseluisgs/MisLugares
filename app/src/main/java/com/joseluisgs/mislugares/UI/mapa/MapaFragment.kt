package com.joseluisgs.mislugares.UI.mapa

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaController
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.fragment_mapa.*


class MapaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Mapa", "Creando Mapa")
        view.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }
        initUI()

    }

    private fun initUI() {
        miMapaProgressBar.visibility = View.VISIBLE
        initMapa()
        miMapaProgressBar.visibility = View.GONE
    }

    /**
     * Inicia el Mapa
     */
    private fun initMapa() {
        Log.i("Mapa", "Iniciando Mapa")
        val mapFragment = (childFragmentManager
            .findFragmentById(R.id.miMapa) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)
    }

    /**
     * EL mapa está listo
     * @param googleMap GoogleMap
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        configurarIUMapa()
        puntosEnMapa()
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
        // mMap.setMinZoomPreference(12.0f)
    }

    fun puntosEnMapa() {
        // Obtenemos los lugares
        val listaLugares = LugarController.selectAll()
        // Por cada lugar, añadimos su marcador
        // Ademamas vamos a calcular la langitud y la latitud media
        listaLugares?.forEach {
            añadirMarcador(it)
        }
        // Actualiazmos la camara para que los cubra a todos
        actualizarCamara(listaLugares)
        // Añadimos los eventos
        mMap.setOnMarkerClickListener(this)

    }

    /**
     * Actauliza la camara para que lso cubra a todos
     * @param listaLugares MutableList<Lugar>?
     */
    private fun actualizarCamara(listaLugares: MutableList<Lugar>?) {
        val bc = LatLngBounds.Builder()
        for (item in listaLugares!!) {
            bc.include(LatLng(item.latitud.toDouble(), item.longitud.toDouble()))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 120))
    }

    /**
     * Creamos el marcador
     * @param lugar Lugar
     */
    private fun añadirMarcador(lugar: Lugar) {
        val posicion = LatLng(lugar.latitud.toDouble(), lugar.longitud.toDouble())
        val pin: Bitmap = crearPin(lugar.imagenID)!!
        val marker = mMap.addMarker(
            MarkerOptions() // Posición
                .position(posicion) // Título
                .title(lugar.nombre) // Subtitulo
                .snippet(lugar.tipo + " del " + lugar.fecha) // Color o tipo d icono
                .anchor(0.5f, 0.907f)
                .icon(BitmapDescriptorFactory.fromBitmap(pin))
        )
        // Le aádo como tag el lugar para recuperarlo
        marker.tag = lugar
    }

    /**
     * Evento on lck sobre el marcador
     * @param marker Marker
     * @return Boolean
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        var lugar = marker.tag as Lugar
        Log.i("Mapa", lugar.toString())
        // Si quiero sacar un mensaje es así
        Toast.makeText(
            context, marker.title.toString() +
                    " Mal sitio para ir.",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    /**
     * Crea un pin personalizado usando la id de la foto
     * @param imagenID String
     * @return Bitmap?
     */
    private fun crearPin(imagenID: String): Bitmap? {
        val fotografia = FotografiaController.selectById(imagenID)
        var result: Bitmap? = null
        try {
            result = Bitmap.createBitmap(dp(62f), dp(76f), Bitmap.Config.ARGB_8888)
            result.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(result)
            val drawable = ContextCompat.getDrawable(context!!, R.drawable.map_pin)
            drawable?.setBounds(0, 0, dp(62f), dp(76f))
            drawable?.draw(canvas)
            val roundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val bitmapRect = RectF()
            canvas.save()
            val bitmap = ImageBase64.toBitmap(fotografia?.imagen.toString())
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                val matrix = Matrix()
                val scale = dp(52f) / bitmap.width.toFloat()
                matrix.postTranslate(dp(5f).toFloat(), dp(5f).toFloat())
                matrix.postScale(scale, scale)
                roundPaint.shader = shader
                shader.setLocalMatrix(matrix)
                bitmapRect[dp(5f).toFloat(), dp(5f).toFloat(), dp(52f + 5).toFloat()] = dp(52f + 5).toFloat()
                canvas.drawRoundRect(bitmapRect, dp(26f).toFloat(), dp(26f).toFloat(), roundPaint)
            }
            canvas.restore()
            try {
                canvas.setBitmap(null)
            } catch (e: Exception) {
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result
    }

    // Densidad de pantalla
    fun dp(value: Float): Int {
        return if (value == 0f) {
            0
        } else
            Math.ceil((resources.displayMetrics.density * value).toDouble()).toInt()
    }
}