package com.joseluisgs.mislugares.Entidades.Backup

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaDTO
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaMapper
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarDTO
import com.joseluisgs.mislugares.Entidades.Lugares.LugarMapper
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
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
    fun exportarDatos(context: Context, usuario: Usuario): Boolean {
        // Recojo los datos
        // Primero fotografías
        this.USUARIO = usuario
        recuperarFotografias(context)
        recuperarLugares(context)
        BACKUP = Backup(
            usuario = this.USUARIO,
            lugares = this.LUGARES,
            fotografias = this.FOTOGRAFIAS
        )
        // Creo el objeto JSON
        val backupJSON = Gson().toJson(BACKUP)
        // Archivo el objeto JSON
        Log.i("REST", this.USUARIO.nombre)
        Log.i("REST", this.LUGARES.size.toString())
        Log.i("REST", this.FOTOGRAFIAS.size.toString())
        //return false;
        return archivar(context, backupJSON.toString())
    }

    /**
     * Recupera los lugares
     * @param context Context
     */
    private fun recuperarLugares(context: Context) {
        val clientREST = MisLugaresAPI.service
        // Ontenemos los lugares filtrados por el usuario, para no mostrar otros.
        val call: Call<List<LugarDTO>> = clientREST.lugarGetAllByUserID(USUARIO.id)
        call.enqueue((object : Callback<List<LugarDTO>> {

            override fun onResponse(call: Call<List<LugarDTO>>, response: Response<List<LugarDTO>>) {
                if (response.isSuccessful) {
                    Log.i("REST", "LugaresGetAll ok")
                    LUGARES = (LugarMapper.fromDTO(response.body() as MutableList<LugarDTO>)) as MutableList<Lugar>
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

    /**
     * Recupera las fotografias
     */
    private fun recuperarFotografias(context: Context) {
        val clientREST = MisLugaresAPI.service
        // Ontenemos los lugares filtrados por el usuario, para no mostrar otros.
        val call: Call<List<FotografiaDTO>> = clientREST.fotografiaGetAllByUserID(USUARIO.id)
        call.enqueue((object : Callback<List<FotografiaDTO>> {

            override fun onResponse(call: Call<List<FotografiaDTO>>, response: Response<List<FotografiaDTO>>) {
                if (response.isSuccessful) {
                    Log.i("REST", "fotografiasGetAll ok")
                    FOTOGRAFIAS = (FotografiaMapper.fromDTO(response.body() as MutableList<FotografiaDTO>)) as MutableList<Fotografia>
                } else {
                    Log.i("REST", "Error: LugaresGetAll isSuccessful")
                }
            }

            override fun onFailure(call: Call<List<FotografiaDTO>>, t: Throwable) {
                Toast.makeText(context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))
    }

    /**
     * Importa os datos
     * @param context Context
     * @return Boolean
     */
    fun importarDatos(context: Context): Boolean {
        val input = importar(context)
        BACKUP = Gson().fromJson(input, Backup::class.java)
        return procesarImportar()
    }

    /**
     * Procesa el exportar los datos
     * @param backup Backup
     * @return Boolean
     */
    private fun procesarImportar(): Boolean {
        // Vamos a insertar el usuario,
        try {
            eliminarDatos()
            insertarDatos()
            return true
        }catch(ex: Exception) {
            Log.i("Backup", "Error: " +ex.localizedMessage)
            return false
        }
    }

    /**
     * Inserta los datos
     */
    private fun insertarDatos() {
        BACKUP.lugares.forEach { insertarLugar(it) }
        BACKUP.fotografias.forEach { insertarFotografia(it) }
    }

    /**
     * Inserta una fotografia
     * @param it Fotografia
     */
    private fun insertarFotografia(fotografia: Fotografia) {
        val clientREST = MisLugaresAPI.service
        val call: Call<FotografiaDTO> = clientREST.fotografiaPost((FotografiaMapper.toDTO(fotografia)))
        call.enqueue((object : Callback<FotografiaDTO> {

            override fun onResponse(call: Call<FotografiaDTO>, response: Response<FotografiaDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "fotografiaPost ok")
                } else {
                    Log.i("REST", "Error fotografiaPost isSeccesful")
                }
            }

            override fun onFailure(call: Call<FotografiaDTO>, t: Throwable) {
                Log.i("REST", "Error al acceder al servicio: " + t.localizedMessage)
            }
        }))
    }

    /**
     * Inserta un Lugar
     * @param it Lugar
     */
    private fun insertarLugar(lugar: Lugar) {
        val clientREST = MisLugaresAPI.service
        val call: Call<LugarDTO> = clientREST.lugarPost((LugarMapper.toDTO(lugar)))
        call.enqueue((object : Callback<LugarDTO> {

            override fun onResponse(call: Call<LugarDTO>, response: Response<LugarDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "lugarPost ok")
                } else {
                    Log.i("REST", "Error lugarPost isSeccesful")
                    Log.i("Insertar", "Error al insertar: " + response.message())
                }
            }

            override fun onFailure(call: Call<LugarDTO>, t: Throwable) {
                Log.i("REST", "Error al acceder al servicio: " + t.localizedMessage)
            }
        }))
    }

    /**
     * Elimina los datos existentes
     */
    private fun eliminarDatos() {
        // Podría lanzar por dos lados eliminar fotografía y lugar, pero si falla, voy de uno en uno y no dejo nada suelto, o eso espero :)
        BACKUP.lugares.forEach{
            eliminarLugar(it)
        }
//        BACKUP.fotografias.forEach {
//            eliminarFotografia(it.id)
//        }
    }

    /**
     * Elimina un lugar
     * @param it Lugar
     */
    private fun eliminarLugar(lugar: Lugar) {
        eliminarFotografia(lugar.imagenID)
        // Borramos el lugar
        val clientREST = MisLugaresAPI.service
        val call: Call<LugarDTO> = clientREST.lugarDelete(lugar.id)
        call.enqueue((object : Callback<LugarDTO> {

            override fun onResponse(call: Call<LugarDTO>, response: Response<LugarDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "lugarDelete ok")
                } else {
                    Log.i("REST", "Error: lugarDelete isSuccessful")
                    Log.i("Eliminar", "Error al eliminar: " + response.message())
                }
            }

            override fun onFailure(call: Call<LugarDTO>, t: Throwable) {
                Log.i("REST", "Error al acceder al servicio: " + t.localizedMessage)
            }
        }))
    }


    /**
     * Elimina una fotografia
     * @param it Fotografia
     */
    private fun eliminarFotografia(fotografiaID: String) {
        val clientREST = MisLugaresAPI.service
        val call: Call<FotografiaDTO> = clientREST.fotografiaDelete((fotografiaID))
        call.enqueue((object : Callback<FotografiaDTO> {

            override fun onResponse(call: Call<FotografiaDTO>, response: Response<FotografiaDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "fotografiaDelete ok")
                } else {
                    Log.i("REST", "Error: fotografiaDelete isSuccessful")
                }
            }

            override fun onFailure(call: Call<FotografiaDTO>, t: Throwable) {
                Log.i("REST", "Error al acceder al servicio: " + t.localizedMessage)
            }
        }))
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
            Log.i("Backup", "Error: " +ex.localizedMessage)
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
