package com.joseluisgs.mislugares.UI.backup

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.joseluisgs.mislugares.Entidades.Backup.Backup
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_backup.*
import kotlinx.coroutines.*
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

class BackupFragment : Fragment() {
    // Firebase
    private lateinit var USUARIO: Usuario
    private lateinit var LUGARES: MutableList<Lugar>
    private lateinit var FOTOGRAFIAS: MutableList<Fotografia>
    private lateinit var BACKUP: Backup
    private var FireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var Auth: FirebaseAuth = Firebase.auth

    private var RES = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FireStore = FirebaseFirestore.getInstance()
        Auth = Firebase.auth
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    /**
     * Inicia la IU
     */
    private fun initUI() {
        backupProgressBar.visibility = View.GONE
        backupUltimaText.text = fechaUltimoBackup()
        backupArchivarImage.setOnClickListener { exportar() }
        backupImportarImage.visibility = View.GONE
        backupImportarText.visibility = View.GONE
    }

    /**
     * Exportar la información
     */
    private fun exportar() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_exportar)
            .setTitle("Exportar datos")
            .setMessage("¿Desea exportar los datos? Se sobreescribirá el último fichero de copia de seguridad")
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which -> exportarDatos() }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    /**
     * Exportar los datos
     */
    private fun exportarDatos() {
        // Se archiva y si todo va bien se da un mensaje y cambia
        backupProgressBar.visibility = View.VISIBLE
        procesarDatos()
    }

    /**
     * Procesa la llamada de los datos en cascada, por el sistema de tareas de Firebase
     */
    private fun procesarDatos() {
        recuperarUsuario()
    }

    /**
     * Mensaje
     */
    fun mensaje(titulo: String, texto: String) {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_exportar)
            .setTitle(titulo)
            .setMessage(texto)
            .setPositiveButton(getString(R.string.aceptar), null)
            .show()

    }

    /**
     * Recupera un usuario de la Base de Datos
     * @param context Context
     */
    private fun recuperarUsuario() {
        USUARIO = Usuario()
        FireStore.collection("usuarios").whereEqualTo("id", Auth.currentUser?.uid).get()
            .addOnSuccessListener { result ->
                USUARIO = result.documents[0].toObject(Usuario::class.java)!!
                recuperarLugares()
            }
            .addOnFailureListener { exception ->
                RES = false
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
    private fun recuperarLugares() {
        LUGARES = mutableListOf()
        FireStore.collection("lugares").whereEqualTo("usuarioID", Auth.currentUser?.uid).get()
            .addOnSuccessListener { result ->
                LUGARES = result.toObjects(Lugar::class.java)
                recuperarFotografias()
            }
            .addOnFailureListener { exception ->
                RES = false
                Toast.makeText(context,
                    "Error al acceder al servicio: " + exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
    }

    /**
     * Recupera las fotografias
     */
    private fun recuperarFotografias() {
        FOTOGRAFIAS = mutableListOf()
        FireStore.collection("imagenes").whereEqualTo("usuarioID", Auth.currentUser?.uid).get()
            .addOnSuccessListener { result ->
                FOTOGRAFIAS = result.toObjects(Fotografia::class.java)
                RES = true
                almacenarDatos()
            }
            .addOnFailureListener { exception ->
                RES = false
                Toast.makeText(context,
                    "Error al acceder al servicio: " + exception.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
    }

    /**
     * Almacena los datos
     */
    private fun almacenarDatos() {
        if (RES) {
            BACKUP = Backup(USUARIO, LUGARES, FOTOGRAFIAS)
            val backupJSON = Gson().toJson(BACKUP)
            RES = archivar(backupJSON.toString())
        }
        if (RES) {
            mensaje("Exportar datos", "Copia de Seguridad guardada con éxito")
        } else {
            mensaje("Error Exportar datos", "Ha habido un error al exportar los datos")
        }
        backupProgressBar.visibility = View.INVISIBLE
    }

    /**
     * Obtiene la fecha del fichero
     * @param context Context
     * @return String
     */
    fun fechaUltimoBackup(): String {
        return leerPropiedades()
    }

    /**
     * Lee las propiedades de un fichero
     * @param context Context
     * @return String
     */
    fun leerPropiedades(): String {
        val dirBackup =
            File((context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath) + "/MisLugaresBack")
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

    /**
     * Archiva los datos
     * @param context Context
     * @param datos String
     * @return Boolean
     */
    fun archivar(datos: String): Boolean {
        val dirBackup =
            File((context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath) + "/MisLugaresBack")
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
}