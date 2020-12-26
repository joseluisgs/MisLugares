package com.joseluisgs.mislugares.UI.lugares

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.CaptureActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_importar_lugar.*
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.util.*

class LugarImportarFragment : Fragment(), OnMapReadyCallback {
    // Firebase
    private lateinit var Auth: FirebaseAuth
    private lateinit var FireStore: FirebaseFirestore
    private lateinit var Storage: FirebaseStorage

    private lateinit var USUARIO: FirebaseUser
    private lateinit var LUGAR: Lugar
    private lateinit var mMap: GoogleMap
    private var posicion: LatLng? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var FOTO: Bitmap
    private lateinit var IMAGEN_URI: Uri
    private var LUGAR_FOTOGRAFIA: Fotografia? = null

    companion object {
        private const val TAG = "Importar"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_importar_lugar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Servicios de Firebase
        Auth = Firebase.auth
        FireStore = FirebaseFirestore.getInstance()
        Storage = FirebaseStorage.getInstance()

        view.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }
        this.USUARIO = Auth.currentUser!!
        initUI()
        scanQRCode()
        // Iniciamos la interfaz
    }

    private fun initUI() {
        importarProgressBar.visibility = View.INVISIBLE
        importarFabAccion.setImageResource(R.drawable.ic_qr_code)
        importarFabAccion.backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.qrCodeColor)
        importarFabAccion.setOnClickListener { scanQRCode() }
        importarLugarTextNombre.visibility = View.INVISIBLE
        importarLugarTextTipo.visibility = View.INVISIBLE
        importarLugarTextFecha.visibility = View.INVISIBLE
        importarLugarImagen.visibility = View.INVISIBLE
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
            } else {
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
        importarProgressBar.visibility = View.VISIBLE
        importarLugarTextNombre.visibility = View.VISIBLE
        importarLugarTextTipo.visibility = View.VISIBLE
        importarLugarTextFecha.visibility = View.VISIBLE
        importarLugarImagen.visibility = View.VISIBLE

        importarLugarTextNombre.text = LUGAR.nombre
        importarLugarTextTipo.text = LUGAR.tipo
        importarLugarTextFecha.text = LUGAR.fecha
        importarFabAccion.setImageResource(R.drawable.ic_importar)
        importarFabAccion.backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.importColor)
        importarFabAccion.setOnClickListener { importarLugar() }

        mapFragment.view?.visibility = View.VISIBLE
        initMapa()
        puntoEnMapa()
        cargarFotografia()
        importarProgressBar.visibility = View.INVISIBLE
    }

    /**
     * Precondiciones de importar
     */
    private fun importarLugar() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_importar)
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
        importarProgressBar.visibility = View.VISIBLE
        val fotografiaID =  UUID.randomUUID().toString()

        // Insertamos lugar
        LUGAR = Lugar(
            id = UUID.randomUUID().toString(),
            nombre = LUGAR.nombre,
            tipo = LUGAR.tipo,
            fecha = LUGAR.fecha,
            latitud = LUGAR.latitud,
            longitud = LUGAR.longitud,
            imagenID = fotografiaID,
            favorito = false,
            votos = 0,
            time = Instant.now().toString(),
            usuarioID = USUARIO.uid
        )
        FireStore.collection("lugares")
            .document(LUGAR.id)
            .set(LUGAR)
            .addOnSuccessListener {
                insertarFotografia(fotografiaID)
                Log.i(TAG, "Lugar importado con éxito con id: $LUGAR")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error insertar lugar", e) }
    }

    /**
     * Carga la fotografía del lugar
     */
    private fun cargarFotografia() {
        val docRef = FireStore.collection("imagenes").document(LUGAR?.imagenID.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    LUGAR_FOTOGRAFIA = document.toObject(Fotografia::class.java)
                    Log.i(TAG, "fotografiasGetById ok: ${document.data}")
                    IMAGEN_URI = Uri.parse(LUGAR_FOTOGRAFIA!!.uri)
                    Picasso.get()
                        .load(LUGAR_FOTOGRAFIA?.uri)
                        .into(importarLugarImagen, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                FOTO = (importarLugarImagen.drawable as BitmapDrawable).bitmap
                            }
                            override fun onError(ex: Exception?) {
                                Log.i(TAG, "Error: Descargar fotografia Picasso")
                            }
                        })
                } else {
                    Log.i(TAG, "Error: No exite fotografía")
                    imagenPorDefecto()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "ERROR: " + exception.localizedMessage)
                imagenPorDefecto()
            }
    }

    /**
     * Fotografía por defecto
     */
    private fun imagenPorDefecto() {
        detalleLugarImagen.setImageBitmap(BitmapFactory.decodeResource(context?.resources,
            R.drawable.ic_mapa))
    }

    /**
     * Inserta una fotografía
     */
    private fun insertarFotografia(fotografiaID: String) {
        // Subimos la fotografía y obtenemos su URL
        val storageRef = Storage.reference
        val baos = ByteArrayOutputStream()
        FOTO.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val lugarImagesRef = storageRef.child("images/$fotografiaID.jpg")
        val uploadTask = lugarImagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.i(TAG, "storage:failure: "+ it.localizedMessage)
            Toast.makeText(context, "Error: " + it.localizedMessage,
                Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // Si se sube la imagen insertamos la foto
            Log.i(TAG, "storage:ok insert")
            // Necesitamos su URI Publica para poder almacenarla
            val downloadUri = taskSnapshot.metadata!!.reference!!.downloadUrl;
            downloadUri.addOnSuccessListener {
                LUGAR_FOTOGRAFIA = Fotografia(
                    id = fotografiaID,
                    time = Instant.now().toString(),
                    usuarioID = USUARIO.uid,
                    uri = it.toString()
                )
                FireStore.collection("imagenes")
                    .document(fotografiaID)
                    .set(LUGAR_FOTOGRAFIA!!)
                    .addOnSuccessListener {
                        importarProgressBar.visibility = View.INVISIBLE
                        Log.i(TAG, "Fotografia insertada con éxito")
                        Snackbar.make(view!!, "¡Lugar importado con éxito!", Snackbar.LENGTH_LONG).show()
                        initUI()
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error al insertar fotografía", e) }
            }
        }
    }

    /**
     * Inicia el Mapa
     */
    private fun initMapa() {
        Log.i("Mapa", "Iniciando Mapa")
        importarProgressBar.visibility = View.VISIBLE
        mapFragment = (childFragmentManager
            .findFragmentById(R.id.importarLugarMapa) as SupportMapFragment?)!!
        mapFragment.getMapAsync(this)
        importarProgressBar.visibility = View.INVISIBLE
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