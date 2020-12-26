package com.joseluisgs.mislugares.UI.mapa

import android.app.AlertDialog
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*
import kotlinx.android.synthetic.main.fragment_mapa.*


class MapaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // Firebase
    private lateinit var Auth: FirebaseAuth
    private lateinit var FireStore: FirebaseFirestore

    private lateinit var mMap: GoogleMap
    private lateinit var USUARIO: FirebaseUser

    companion object {
        private const val TAG = "Mapa"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Servicios de Firebase
        Auth = Firebase.auth
        FireStore = FirebaseFirestore.getInstance()

        Log.i(TAG, "Creando Mapa")
        view.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }

        this.USUARIO = Auth.currentUser!!
        initUI()

    }

    private fun initUI() {
        miMapaProgressBar.visibility = View.GONE
        initMapa()
    }

    /**
     * Inicia el Mapa
     */
    private fun initMapa() {
        miMapaProgressBar.visibility = View.VISIBLE
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
        // Obtenemos los lugares del usuario
        FireStore.collection("lugares")
            .whereEqualTo("usuarioID", USUARIO.uid)
            .get()
            .addOnSuccessListener { result ->
                Log.i(TAG, "LugaresGetAll ok")
                val listaLugares = mutableListOf<Lugar>()
                for (document in result) {
                    val miLugar = document.toObject(Lugar::class.java)
                    listaLugares.add(miLugar);
                }
                procesarLugares(listaLugares)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,
                    "Error al acceder al servicio: " + exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun procesarLugares(listaLugares: MutableList<Lugar>) {
        listaLugares.forEach {
            añadirMarcador(it)
        }
        // Actualiazmos la camara para que los cubra a todos
        actualizarCamara(listaLugares)
        // Añadimos los eventos
        mMap.setOnMarkerClickListener(this)
        miMapaProgressBar.visibility = View.GONE
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
        // Buscamos la fotografia
        val docRef = FireStore.collection("imagenes").document(lugar.imagenID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val miImagen = document.toObject(Fotografia::class.java)
                    val posicion = LatLng(lugar.latitud.toDouble(), lugar.longitud.toDouble())
                    val imageView = ImageView(context);
                    Picasso.get()
                        .load(miImagen?.uri)
                        .into(imageView, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                val temp = (imageView.drawable as BitmapDrawable).bitmap
                                val pin: Bitmap = crearPin(temp)!!
                                val marker = mMap.addMarker(
                                    MarkerOptions() // Posición
                                        .position(posicion) // Título
                                        .title(lugar.nombre) // Subtitulo
                                        .snippet(lugar.tipo + " del " + lugar.fecha) // Color o tipo d icono
                                        .anchor(0.5f, 0.907f)
                                        .icon(BitmapDescriptorFactory.fromBitmap(pin))
                                )
                                // Le añado como tag el lugar para recuperarlo
                                marker.tag = lugar
                            }
                            override fun onError(e: Exception) {
                                Log.d(TAG, "Error al descargar imagen")
                            }
                        })

                } else {
                    Log.i(TAG, "Error: No exite fotografía")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "ERROR: " + exception.localizedMessage)
            }
    }

    /**
     * Evento on lck sobre el marcador
     * @param marker Marker
     * @return Boolean
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        val lugar = marker.tag as Lugar
        Log.i("Mapa", lugar.toString())
        mostrarDialogo(lugar)
        return false
    }

    /**
     * Muestra un dialogo del lugar
     * @param lugar Lugar
     */
    private fun mostrarDialogo(lugar: Lugar) {
        val builder = AlertDialog.Builder(context)
        val inflater = requireActivity().layoutInflater
        val vista = inflater.inflate(R.layout.intem_visualizacion_mapa, null)
        // Le ponemos las cosas
        val nombre = vista.findViewById(R.id.mapaLugarTextNombre) as TextView
        nombre.text = lugar.nombre
        val tipo = vista.findViewById(R.id.mapaLugarTextTipo) as TextView
        tipo.text = lugar.tipo
        val fecha = vista.findViewById(R.id.mapaLugarTextFecha) as TextView
        fecha.text = lugar.fecha
        val imagen = vista.findViewById(R.id.mapaLugarImagen) as ImageView
        val docRef = FireStore.collection("imagenes").document(lugar.imagenID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val miImagen = document.toObject(Fotografia::class.java)
                    Picasso
                        .get()
                        .load(miImagen?.uri)
                        .into(imagen)
                } else {
                    Log.i(TAG, "Error: No exite fotografía")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "ERROR: " + exception.localizedMessage)
            }

        builder
            .setView(vista)
            .setIcon(R.drawable.ic_location)
            .setTitle("Lugar")
            // Add action buttons
            .setPositiveButton(R.string.aceptar) { _, _ ->
                null
            }
        //.setNegativeButton(R.string.cancelar, null)
        // setNeutralButton("Maybe", neutralButtonClick)
        builder.show()
    }

    /**
     * Crea un pin personalizado usando la id de la foto
     * @param imagenID String
     * @return Bitmap?
     */
    private fun crearPin(bitmap: Bitmap): Bitmap? {
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