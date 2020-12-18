package com.joseluisgs.mislugares.UI.mapa

import android.app.AlertDialog
import android.graphics.*
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
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaDTO
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaMapper
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarDTO
import com.joseluisgs.mislugares.Entidades.Lugares.LugarMapper
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.fragment_mapa.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var USUARIO: Usuario

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

        this.USUARIO = (activity?.application as MyApp).SESION_USUARIO
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
        // Obtenemos los lugares
        val clientREST = MisLugaresAPI.service
        // Ontenemos los lugares filtrados por el usuario, para no mostrar otros.
        val call: Call<List<LugarDTO>> = clientREST.lugarGetAllByUserID(USUARIO.id)
        call.enqueue((object : Callback<List<LugarDTO>> {

            override fun onResponse(call: Call<List<LugarDTO>>, response: Response<List<LugarDTO>>) {
                if (response.isSuccessful) {
                    Log.i("REST", "LugaresGetAll ok")
                    val listaLugares = (LugarMapper.fromDTO(response.body() as MutableList<LugarDTO>)) as MutableList<Lugar>
                    procesarLugares(listaLugares)
                } else {
                    Log.i("REST", "Error: LugaresGetAll isSuccessful")
                }
            }

            override fun onFailure(call: Call<List<LugarDTO>>, t: Throwable) {
                Toast.makeText(context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))
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
        val clientREST = MisLugaresAPI.service
        val call: Call<FotografiaDTO> = clientREST.fotografiaGetById(lugar.imagenID)
        call.enqueue((object : Callback<FotografiaDTO> {

            override fun onResponse(call: Call<FotografiaDTO>, response: Response<FotografiaDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "fotografiasGetById ok")
                    var remoteFotografia = FotografiaMapper.fromDTO(response.body() as FotografiaDTO)
                    val posicion = LatLng(lugar.latitud.toDouble(), lugar.longitud.toDouble())
                    val pin: Bitmap = crearPin(remoteFotografia)!!
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
                } else {
                    Log.i("REST", "Error: fotografiasGetById isSuccessful")
                    // holder.itemLugarImagen.setImageBitmap(BitmapFactory.decodeResource(holder.context?.resources, R.drawable.ic_mapa))
                }
            }

            override fun onFailure(call: Call<FotografiaDTO>, t: Throwable) {
                Toast.makeText(context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))
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

        val clientREST = MisLugaresAPI.service
        val call: Call<FotografiaDTO> = clientREST.fotografiaGetById(lugar.imagenID)
        call.enqueue((object : Callback<FotografiaDTO> {

            override fun onResponse(call: Call<FotografiaDTO>, response: Response<FotografiaDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "fotografiasGetById ok")
                    var remoteFotografia = FotografiaMapper.fromDTO(response.body() as FotografiaDTO)
                    imagen.setImageBitmap(ImageBase64.toBitmap(remoteFotografia.imagen))
                } else {
                    Log.i("REST", "Error: fotografiasGetById isSuccessful")
                    // holder.itemLugarImagen.setImageBitmap(BitmapFactory.decodeResource(holder.context?.resources, R.drawable.ic_mapa))
                }
            }

            override fun onFailure(call: Call<FotografiaDTO>, t: Throwable) {
                Toast.makeText(context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))
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
    private fun crearPin(fotografia: Fotografia): Bitmap? {
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
            val bitmap = ImageBase64.toBitmap(fotografia.imagen.toString())
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