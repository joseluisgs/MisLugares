package com.joseluisgs.mislugares.Entidades.Backup

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * Controllador de Backup
 */
object BackupController {
    private lateinit var USUARIO: Usuario
    private lateinit var LUGARES: MutableList<Lugar>
    private lateinit var FOTOGRAFIAS: MutableList<Fotografia>
    private lateinit var BACKUP: Backup
    private val FireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val Auth: FirebaseAuth =  Firebase.auth

    private const val TAG ="Backup"

    val scope = CoroutineScope(Job() + Dispatchers.Main)

    /**
     * Obtiene la fecha del fichero
     * @param context Context
     * @return String
     */
    fun fechaUltimoBackup(context: Context): String {
        return leerPropiedades(context)
    }

    /**
     * Exporta los datos
     * @param context Context
     * @return Boolean
     */
    fun exportarDatos(context: Context,): Boolean {

        // Recojo los datos
        // Primero fotografías
        scope.launch {
            val tareas = listOf(
                async {recuperarUsuario(context)},
                async {recuperarFotografias(context)},
                async {recuperarLugares(context)}
            )
            tareas.awaitAll()
        }
        // Creo el objeto JSON
        val backupJSON = Gson().toJson(BACKUP)
        // Archivo el objeto JSON
        //Log.i("REST", this.USUARIO.nombre)
        Log.i(TAG, LUGARES.size.toString())
        Log.i(TAG, FOTOGRAFIAS.size.toString())
        //return false;
        return archivar(context, backupJSON.toString())
    }

    // Recupera un usuario de la Base de datos
    private fun recuperarUsuario(context: Context) {
        USUARIO = Usuario()
        FireStore.collection("usuarios").whereEqualTo("id", Auth.currentUser?.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    USUARIO = document.toObject(Usuario::class.java)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,
                    "Error al acceder al servicio: " + exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
    }

    /**
     * Recupera los lugares
     * @param context Context
     */
    private fun recuperarLugares(context: Context) {
        LUGARES = mutableListOf();
        FireStore.collection("lugares").whereEqualTo("usuarioID", Auth.currentUser?.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val miLugar = document.toObject(Lugar::class.java)
                    LUGARES.add(miLugar)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,
                    "Error al acceder al servicio: " + exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
    }

    /**
     * Recupera las fotografias
     */
    private fun recuperarFotografias(context: Context) {
        FOTOGRAFIAS = mutableListOf()
        FireStore.collection("imagenes").whereEqualTo("usuarioID", Auth.currentUser?.uid).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val miImagen = document.toObject(Fotografia::class.java)
                    FOTOGRAFIAS.add(miImagen)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,
                    "Error al acceder al servicio: " + exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
    }


    /**
     * Archiva los datos
     * @param context Context
     * @param datos String
     * @return Boolean
     */
    fun archivar(context: Context, datos: String): Boolean {
        val dirBackup =
            File((context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath) + "/MisLugaresBack")
        if (!dirBackup.exists())
            dirBackup.mkdir()
        val file = File(dirBackup, "backup.json")
        try {
            val strToBytes: ByteArray = datos.toByteArray()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Files.write(Paths.get(file.toURI()), strToBytes)
                return true
            } else
                return false
        } catch (ex: Exception) {
            Log.i("Backup", "Error: " + ex.localizedMessage)
            return false
        }
    }

    /**
     * Importa los datos o desarchiva
     * @param context Context
     * @return String
     */
    fun importar(context: Context): String {
        val dirBackup =
            File((context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath) + "/MisLugaresBack")
        val file = File(dirBackup, "backup.json")
        var datos: String = ""
        if (file.exists()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                datos = Files.readAllLines(Paths.get(file.toURI()))[0]
            }
        }
        return datos
    }

    /**
     * Lee las propiedades de un fichero
     * @param context Context
     * @return String
     */
    fun leerPropiedades(context: Context): String {
        val dirBackup =
            File((context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath) + "/MisLugaresBack")
        val file = File(dirBackup, "backup.json")
        if (file.exists()) {
            val basicfile: BasicFileAttributeView =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Files.getFileAttributeView(
                        Paths.get(file.toURI()),
                        BasicFileAttributeView::class.java, LinkOption.NOFOLLOW_LINKS
                    )
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            val attr: BasicFileAttributes = basicfile.readAttributes()
            val date: Long = attr.creationTime().toMillis()
            val instant: Instant = Instant.ofEpochMilli(date)
            val lt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            return DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss").format(lt)
        }
        return ""
    }
}
