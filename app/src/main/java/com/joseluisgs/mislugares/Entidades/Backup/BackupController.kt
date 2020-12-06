package com.joseluisgs.mislugares.Entidades.Backup

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaController
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.Entidades.Preferencias.PreferenciasController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioController
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

    /**
     * Obtiene la fecha del fichero
     * @param context Context
     * @return String
     */
    fun fechaUltimoBackup(context: Context): String {
        val cad = leerPropiedades(context)
        return cad
    }

    /**
     * Exporta los datos
     * @param context Context
     * @return Boolean
     */
    fun exportarDatos(context: Context): Boolean {
        // Recojo los datos
        val usuarios =  mutableListOf<Usuario>()
        usuarios.add(PreferenciasController.leerSesion(context))
        val lugares = LugarController.selectAll()!!
        val fotografias = FotografiaController.selectAll()!!
        val backup = Backup(
            usuarios = usuarios,
            lugares = lugares,
            fotografias = fotografias
        )
        // Creo el objeto JSON
        val backupJSON = Gson().toJson(backup)
        // Archivo el objeto JSON
        return archivar(context, backupJSON.toString())
    }

    /**
     * Importa os datos
     * @param context Context
     * @return Boolean
     */
    fun importarDatos(context: Context): Boolean {
        val input = importar(context)
        val backup = Gson().fromJson(input, Backup::class.java)
        return procesarImportar(backup)
    }

    /**
     * Procesa el exportar los datos
     * @param backup Backup
     * @return Boolean
     */
    private fun procesarImportar(backup: Backup): Boolean {
        // Vamos a insertar el usuario,
        try {
            eliminarDatos()
            backup.usuarios.forEach { UsuarioController.insert(it) }
            backup.lugares.forEach { LugarController.insert(it) }
            backup.fotografias.forEach { FotografiaController.insert(it) }
            return true
        }catch(ex: Exception) {
            return false
        }
    }

    /**
     * Elimina los datos existentes
     */
    private fun eliminarDatos() {
        UsuarioController.removeAll()
        LugarController.removeAll()
        FotografiaController.removeAll()
    }

    /**
     * Archiva los datos
     * @param context Context
     * @param datos String
     * @return Boolean
     */
    fun archivar(context: Context, datos: String): Boolean {
        val dirBackup = File((context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath) + "/MisLugaresBack")
        if(!dirBackup.exists())
            dirBackup.mkdir()
        val file = File(dirBackup, "backup.json")
        try {
            val strToBytes: ByteArray = datos.toByteArray()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Files.write(Paths.get(file.toURI()), strToBytes)
                return true
            } else
                return false
        }catch (ex: Exception) {
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
