package com.joseluisgs.mislugares.UI.lugares

import Utilidades.Cifrador
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
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
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaController
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.Fotos
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.fragment_lugar_detalle.*
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Clase Lugar Detalle
 */
class LugarDetalleFragment(
    private var LUGAR: Lugar? = null,
    private val MODO: Modo? = Modo.INSERTAR,
    private val ANTERIOR: LugaresFragment? = null,
    private val LUGAR_INDEX: Int? = null,
    ) : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // Mis Variables
    private lateinit var USUARIO: Usuario
    private var PERMISOS: Boolean = false

    // Variables a usar y permisos del mapa
    private lateinit var mMap: GoogleMap
    private var mPosicion: FusedLocationProviderClient? = null
    private var marcadorTouch: Marker? = null
    private var localizacion: Location? = null
    private var posicion: LatLng? = null

    // Variables para la camara
    private val GALERIA = 1
    private val CAMARA = 2
    private lateinit var IMAGEN_URI: Uri
    private val IMAGEN_DIRECTORY = "/MisLugares"
    private val IMAGEN_PROPORCION = 600
    private lateinit var FOTO: Bitmap
    private var IMAGEN_COMPRESION = 60
    private val IMAGEN_PREFIJO = "lugar"
    private val IMAGEN_EXTENSION = ".jpg"

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
        // Y esto que parece una tonteria es para que no se propagen los eventos
        // entre fragments y no disparar eventos de otros fragments
        view.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }
        initIU()
    }

    /**
     * Iniciamos los elementos de la interfaz
     */
    private fun initIU() {
        // Inicializamos las cosas comunes y las específicass
        // Actualizo la vista anterior para que no se quede el swipe marcado
        ANTERIOR?.actualizarVistaLista()
        initPermisos()
        initUsuario()
        // Modos de ejecución
        when (this.MODO) {
            Modo.INSERTAR -> initModoInsertar()
            Modo.VISUALIZAR -> initModoVisualizar()
            Modo.ELIMINAR -> initModoEliminar()
            Modo.ACTUALIZAR -> initModoActualizar()
            else -> {
            }
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
        Log.i("Lugares", "Modo Insertar")
        // Ocultamos o quitamos lo que no queremos ver en este modo
        detalleLugarTextVotos.visibility = View.GONE // View.INVISIBLE
        detalleLugarInputNombre.setText("Tu Lugar Ahora") // Quitar luego!!
        val date = LocalDateTime.now()
        detalleLugarBotonFecha.text = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date)
        detalleLugarBotonFecha.setOnClickListener { escogerFecha() }
        detalleLugarFabAccion.setOnClickListener { insertarLugar() }
        detalleLugarFabCamara.setOnClickListener { initDialogFoto() }

    }

    /**
     * Inicia el modo de Visualizar
     */
    private fun initModoVisualizar() {
        Log.i("Lugares", "Modo Visualizar")
        // Ocultamos o quitamos lo que no queremos ver en este modo
        detalleLugarFabCamara.visibility = View.GONE
        detalleLugarFabAccion.visibility = View.GONE
        detalleLugarInputNombre.setText(LUGAR?.nombre)
        detalleLugarInputNombre.isEnabled = false
        detalleLugarBotonFecha.text = LUGAR?.fecha
        detalleLugarTextVotos.text = LUGAR?.votos.toString() + " voto(s)."
        // detalleLugarInputTipo.setText(LUGAR?.tipo)
        detalleLugarSpinnerTipo.setSelection(
            (detalleLugarSpinnerTipo.adapter as ArrayAdapter<String?>).getPosition(
                LUGAR?.tipo
            )
        )
        detalleLugarSpinnerTipo.isEnabled = false
        val fotografia = FotografiaController.selectById(LUGAR?.imagenID.toString())
        this.FOTO = ImageBase64.toBitmap(fotografia?.imagen.toString())!!
        IMAGEN_URI = Uri.parse(fotografia?.uri)
        detalleLugarImagen.setImageBitmap(this.FOTO)
    }

    /**
     * Inicia el Modo de Modificar
     */
    fun initModoEliminar() {
        Log.i("Lugares", "Modo Eliminar")
        initModoVisualizar()
        detalleLugarFabAccion.visibility = View.VISIBLE
        detalleLugarFabAccion.setImageResource(R.drawable.ic_remove)
        detalleLugarFabAccion.backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.removeColor)
        detalleLugarFabAccion.setOnClickListener { eliminarLugar() }

    }

    fun initModoActualizar() {
        Log.i("Lugares", "Modo Actualizar")
        initModoVisualizar()
        // Actualizo la interfaz
        detalleLugarFabAccion.visibility = View.VISIBLE
        detalleLugarFabAccion.setImageResource(R.drawable.ic_update)
        detalleLugarFabAccion.backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.updateColor)
        // Actualizo la interfaz
        detalleLugarBotonFecha.setOnClickListener { escogerFecha() }
        detalleLugarFabCamara.visibility = View.VISIBLE
        detalleLugarFabCamara.setOnClickListener { initDialogFoto() }
        detalleLugarSpinnerTipo.isEnabled = true
        detalleLugarInputNombre.isEnabled = true
        // Acción
        detalleLugarFabAccion.setOnClickListener { actualizarLugar() }

    }

    /**
     * Precondiciones para insertar
     */
    private fun insertarLugar() {
        if (comprobarFormulario()) {
            alertaDialogo("Insertar Lugar", "¿Desea salvar este lugar?")
        }
    }

    /**
     * Inserta en el sistema de persistencia o almacenamiento
     */
    private fun insertar() {
        try {
            // Iderntamos la fotografia
            val b64 = ImageBase64.toBase64(this.FOTO)!!
            val fotografia = Fotografia(
                imagen = b64,
                uri = IMAGEN_URI.toString(),
                hash = Cifrador.toHash(b64).toString(),
                time = Instant.now().toString(),
                usuarioID = USUARIO.id
            )
            FotografiaController.insert(fotografia)
            Log.i("Insertar", "Fotografía insertada con éxito con: " + fotografia.id)
            // Insertamos lugar
            LUGAR = Lugar(
                nombre = detalleLugarInputNombre.text.toString().trim(),
                tipo = (detalleLugarSpinnerTipo.selectedItem as String),
                fecha = detalleLugarBotonFecha.text.toString(),
                latitud = posicion?.latitude.toString(),
                longitud = posicion?.longitude.toString(),
                imagenID = fotografia.id,
                favorito = false,
                votos = 0,
                time = Instant.now().toString(),
                usuarioID = USUARIO.id
            )
            LugarController.insert(LUGAR!!)
            // Actualizamos el adapter
            ANTERIOR?.insertarItemLista(LUGAR!!)
            Snackbar.make(view!!, "¡Lugar añadido con éxito!", Snackbar.LENGTH_LONG).show();
            Log.i("Insertar", "Lugar insertado con éxito con id" + LUGAR)
            // Forzamos a cargar la lista de lugares
            // ANTERIOR?.cargarLugares()
            // Volvemos
            volver()
        } catch (ex: Exception) {
            Toast.makeText(context, "Error al insertar: " + ex.localizedMessage, Toast.LENGTH_LONG).show()
            Log.i("Insertar", "Error al insertar: " + ex.localizedMessage)
        }
        try {
            IMAGEN_URI.toFile().delete()
            Log.i("Insertar", "Borrada la imagen tempral asociada")
        } catch (ex: Exception) {}
    }

    /**
     * Precondiciones para eliminar
     */
    private fun eliminarLugar() {
        alertaDialogo("Eliminar Lugar", "¿Desea eliminar este lugar?")
    }

    /**
     * Elimina un objeto de la base de datos
     */
    private fun eliminar() {
        try {
            //Eliminamos lógicamente // Eliminamos el lugar
            val fotografiaID = LUGAR?.imagenID.toString()
            val fotografia = FotografiaController.selectById(fotografiaID)
            FotografiaController.delete(fotografia!!)
            LugarController.delete(LUGAR!!)
            // Actualizamos el adapter
            ANTERIOR?.eliminarItemLista(LUGAR_INDEX!!)
            Snackbar.make(view!!, "¡Lugar eliminado con éxito!", Snackbar.LENGTH_LONG).show();
            Log.i("Eliminar", "Lugar eliminado con éxito")
        } catch (ex: Exception) {
            Toast.makeText(context, "Error al eliminar: " + ex.localizedMessage, Toast.LENGTH_LONG).show()
            Log.i("Eliminar", "Error al eliminar: " + ex.localizedMessage)
        }
        // Volvemos
        volver()
    }

    private fun actualizarLugar() {
        if (comprobarFormulario()) {
            alertaDialogo("Modificar Lugar", "¿Desea modificar este lugar?")
        }
    }

    private fun actualizar() {
        try {
            // Actualizamos la fotografía por si hay cambios
            val fotografiaID = LUGAR?.imagenID.toString()
            val fotografia = FotografiaController.selectById(fotografiaID)
            val b64 = ImageBase64.toBase64(this.FOTO)!!
                Log.i("Actualizar", "Imagenes Distintas")
                // Si son distintas actualizamos
                with(fotografia!!) {
                    imagen = b64
                    uri = IMAGEN_URI.toString()
                    hash = Cifrador.toHash(b64).toString()
                    time = Instant.now().toString() // Fecha de la ultima actualización
                    usuarioID = USUARIO.id
                }
                FotografiaController.update(fotografia)
                Log.i("Actualizar", "Fotografía actualizada")
            Log.i("Actualizar", "Actualizamos los lugares")
            with(LUGAR!!) {
                nombre = detalleLugarInputNombre.text.toString().trim()
                tipo = (detalleLugarSpinnerTipo.selectedItem as String)
                fecha = detalleLugarBotonFecha.text.toString()
                latitud = posicion?.latitude.toString()
                longitud = posicion?.longitude.toString()
                time = Instant.now().toString()
            }
            LugarController.update(LUGAR!!)
            // Actualizamos el adapter
            ANTERIOR?.actualizarItemLista(LUGAR!!, LUGAR_INDEX!!)
            Snackbar.make(view!!, "¡Lugar actualizado con éxito!", Snackbar.LENGTH_LONG).show();
            Log.i("Actualizar", "Lugar actualizado con éxito con id" + LUGAR!!.id)
            // Volvemos
            volver()

        } catch (ex: Exception) {
            Toast.makeText(context, "Error al actualizar: " + ex.localizedMessage, Toast.LENGTH_LONG).show()
            Log.i("Actualizar", "Error al actualizar: " + ex.localizedMessage)
        }
        try {
            IMAGEN_URI.toFile().delete()
            Log.i("Modificar", "Borrada la imagen temporal asociada")
        } catch (ex: Exception) {}
    }

    private fun volver() {
        activity?.onBackPressed();
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
     * Dialogo de opciones
     */
    private fun alertaDialogo(titulo: String, texto: String){
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle(titulo)
            setMessage(texto)
            setPositiveButton(R.string.aceptar) { _, _ ->
                when (MODO) {
                    Modo.INSERTAR -> insertar()
                    // VISUALIZAR -> initModoVisualizar
                    Modo.ELIMINAR -> eliminar()
                    Modo.ACTUALIZAR -> actualizar()
                    else -> {
                    }
                }
            }
            setNegativeButton(R.string.cancelar, null)
            // setNeutralButton("Maybe", neutralButtonClick)
            show()
        }
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
        if (!this::FOTO.isInitialized ) {
            this.FOTO = (detalleLugarImagen.drawable as BitmapDrawable).bitmap
            Toast.makeText(context, "La imagen no puede estar vacía", Toast.LENGTH_SHORT).show()
            sal = false
        }
        return sal
    }

    /**
     * FUNCIONALIDAD DEL GPS
     */

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
            Modo.VISUALIZAR -> mapaVisualizar()
            Modo.ELIMINAR -> mapaVisualizar()
            Modo.ACTUALIZAR -> mapaActualizar()
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
     * Modo Mapa Visualizar
     */
    private fun mapaVisualizar() {
        // Vamos a dejar que nos deje ir a l lugar obteniendo la psoición actual
        // mMap.isMyLocationEnabled = true;
        // procesamos el mapa moviendo la camara allu
        Log.i("Mapa", "Configurando Modo Visualizar")
        posicion = LatLng(LUGAR!!.latitud.toDouble(), LUGAR!!.longitud.toDouble())
        // marcadorTouch?.remove()
         mMap.addMarker(
            MarkerOptions() // Posición
                .position(posicion!!) // Título
                .title(LUGAR!!.nombre) // Subtitulo
                .snippet(LUGAR!!.tipo + " del " + LUGAR!!.fecha) // Color o tipo d icono
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion))
    }

    /**
     * Modo Mapa Actualizar
     */
    private fun mapaActualizar() {
        Log.i("Mapa", "Configurando Modo Actualizar")
        if (this.PERMISOS) {
            mMap.isMyLocationEnabled = true
        }
        activarEventosMarcadores()
        mapaVisualizar()
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
                    .title("Posición Actual") // Subtitulo
                    .snippet(detalleLugarInputNombre.text.toString()) // Color o tipo d icono
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
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
                        //try {
                        localizacion = task.result
                        posicion = LatLng(
                            localizacion!!.latitude,
                            localizacion!!.longitude
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion));
                        //}catch (ex: Exception) {
                        //   Snackbar.make(view!!, "GPS Inactivo o sin posición actual", Snackbar.LENGTH_LONG).show();
                        //}
                    } else {
                        Log.i("GPS", "No se encuetra la última posición.")
                        Log.e("GPS", "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Snackbar.make(
                view!!,
                "No se ha encontrado su posoción actual o el GPS está desactivado",
                Snackbar.LENGTH_LONG
            ).show();
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
        // Si quiero sacar un mensaje es así
//        Toast.makeText(
//                context, marker.title.toString() +
//                        " Mal sitio para ir.",
//                Toast.LENGTH_SHORT
//            ).show()
        return false
    }

    /**
     * FUNCIONALIDAD DE LA CAMARA
     */

    /**
     * Muestra el diálogo para tomar foto o elegir de la galería
     */
    private fun initDialogFoto() {
        val fotoDialogoItems = arrayOf(
            "Seleccionar fotografía de galería",
            "Capturar fotografía desde la cámara"
        )
        // Creamos el dialog con su builder
        AlertDialog.Builder(context)
            .setTitle("Seleccionar Acción")
            .setItems(fotoDialogoItems) { dialog, modo ->
                when (modo) {
                    0 -> elegirFotoGaleria()
                    1 -> tomarFotoCamara()
                }
            }
            .show()
    }

    /**
     * Elige una foto de la galeria
     */
    private fun elegirFotoGaleria() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALERIA)
    }

    //Llamamos al intent de la camara
    // https://developer.android.com/training/camera/photobasics.html#TaskPath
    private fun tomarFotoCamara() {
        // Si queremos hacer uso de fotos en alta calidad
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val nombre = Fotos.crearNombreFoto(IMAGEN_PREFIJO, IMAGEN_EXTENSION)
        val fichero = Fotos.salvarFoto(IMAGEN_DIRECTORY, nombre, context!!)
        IMAGEN_URI = Uri.fromFile(fichero)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGEN_URI)
        Log.i("Camara", IMAGEN_URI.path.toString())
        startActivityForResult(intent, CAMARA)
    }

    /**
     * Siempre se ejecuta al realizar una acción
     * @param requestCode Int
     * @param resultCode Int
     * @param data Intent?
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("FOTO", "Opción::--->$requestCode")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            Log.i("FOTO", "Se ha cancelado")
        }
        // Procesamos la foto de la galeria
        if (requestCode == GALERIA) {
            Log.i("FOTO", "Entramos en Galería")
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                val contentURI = data.data!!
                try {
                    // Obtenemos el bitmap de su almacenamiento externo
                    // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    if (Build.VERSION.SDK_INT < 28) {
                        this.FOTO = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI);
                    } else {
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(context?.contentResolver!!, contentURI)
                        this.FOTO = ImageDecoder.decodeBitmap(source)
                    }
                    // Para jugar con las proporciones y ahorrar en memoria no cargando toda la foto, solo carga 600px max
                    val prop = this.IMAGEN_PROPORCION / this.FOTO.width.toFloat()
                    // Actualizamos el bitmap para ese tamaño, luego podríamos reducir su calidad
                    this.FOTO = Bitmap.createScaledBitmap(
                        this.FOTO,
                        this.IMAGEN_PROPORCION,
                        (this.FOTO.height * prop).toInt(),
                        false
                    )
                    // Vamos a copiar nuestra imagen en nuestro directorio comprimida por si acaso.
                    val nombre = Fotos.crearNombreFoto(IMAGEN_PREFIJO, IMAGEN_EXTENSION)
                    val fichero =
                        Fotos.copiarFoto(this.FOTO, nombre, IMAGEN_DIRECTORY, IMAGEN_COMPRESION, context!!)
                    IMAGEN_URI = Uri.fromFile(fichero)
                    // Por su queemos guardar el URI con la que se almacena en la Mediastore
                    // Pero no lo hacemos porqe ya está en la galería
                    // IMAGEN_URI = Fotos.añadirFotoGaleria(IMAGEN_URI, IMAGEN_NOMBRE, context!!)!!
                    Toast.makeText(context, "¡Foto rescatada de la galería!", Toast.LENGTH_SHORT).show()
                    detalleLugarImagen.setImageBitmap(this.FOTO)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "¡Fallo Galeria!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMARA) {
            Log.i("FOTO", "Entramos en Camara")
            // Cogemos la imagen, pero podemos coger la imagen o su modo en baja calidad (thumbnail)
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    this.FOTO = MediaStore.Images.Media.getBitmap(context?.contentResolver, IMAGEN_URI)
                } else {
                    val source: ImageDecoder.Source = ImageDecoder.createSource(context?.contentResolver!!, IMAGEN_URI)
                    this.FOTO = ImageDecoder.decodeBitmap(source)
                }
                // Comprimimos la foto
                Log.i("Camara", IMAGEN_URI.path.toString())
                Fotos.comprimirFoto(IMAGEN_URI.toFile(), this.FOTO, this.IMAGEN_COMPRESION)
                // Mostramos
                detalleLugarImagen.setImageBitmap(this.FOTO)
                Toast.makeText(context, "¡Foto Salvada!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "¡Fallo Camara!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}