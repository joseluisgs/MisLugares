package com.joseluisgs.mislugares.Entidades.Sesiones

import android.content.Context
import android.util.Log
import com.joseluisgs.mislugares.Entidades.Preferencias.PreferenciasController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Services.MisLugaresAPI
import com.joseluisgs.mislugares.Services.MisLugaresREST
import io.realm.Realm
import io.realm.kotlin.where
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
* Controlador de Sesion
*/
object SesionController  {

    fun getLocal(context: Context): Usuario? {
        if(PreferenciasController.comprobarSesion(context)) {
            try {
                return PreferenciasController.leerSesion(context)
            } catch (ex: Exception) {
                Log.i("Sesion", "Error al leer sesion: " + ex.localizedMessage)
                return null
            }
        }
        return null
    }

//    fun selectById(usuarioID: String, sesion: Sesion): {
//        clientREST = MisLugaresAPI.service
//        val call: Call<SesionDTO> = clientREST.sesionById(usuarioID)
//        call.enqueue((object : Callback<SesionDTO> {
//            override fun onResponse(call: Call<SesionDTO>, response: Response<SesionDTO>) {
//                // Si ok
//                if (response.isSuccessful) {
//                   Log.i("REST", "SesionByID ok")
//                    var remoteSesion = SesionMapper.fromDTO(response.body() as SesionDTO)
//                    sesion.fromSesion(remoteSesion)
//                } else {
//                    Log.i("REST", "Error: SesionByID isSuccessful")
//                }
//            }
//            override fun onFailure(call: Call<SesionDTO>, t: Throwable) {
//                Log.i("REST", "Error: SesionByID on Failure")
//            }
//        }))
//        return
//    }



     /* Inserta una sesion
     * @param sesion Sesion
     */
    fun insert(sesion: Sesion) {
        Realm.getDefaultInstance().executeTransaction {
            it.copyToRealm(sesion); // Copia, inserta
        }
    }

    /**
     * Borra un lugar del sistema
     * @param sesion Sesion
     */
    fun delete(sesion: Sesion) {
        Realm.getDefaultInstance().executeTransaction {
            it.where<Sesion>().equalTo("usuarioID", sesion.usuarioID).findFirst()?.deleteFromRealm()
        }
    }

    fun deleteByID(usuarioID: String) {
        Realm.getDefaultInstance().executeTransaction {
            it.where<Sesion>().equalTo("usuarioID", usuarioID).findFirst()?.deleteFromRealm()
        }
    }


    /**
     * Busca lugar por id
     * @param login String
     * @return Lugar?
     */
    fun selectById(usuarioID: String): Sesion? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Sesion>().equalTo("usuarioID", usuarioID).findFirst()
        )
    }

    /**
     * Te devuelve la sesion actual
     * @return Sesion?
     */
    fun getFirst(): Sesion? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Sesion>().findFirst()
        )
    }

    /**
     * Te devuelve la sesion actual
     * @return Sesion?
     */
    fun selectAll(): MutableList<Sesion>? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Sesion>().findAll()
        )
    }

    /**
     * Elimina todos los objetos
     */
    fun removeAll() {
        Realm.getDefaultInstance().executeTransaction {
            Realm.getDefaultInstance().where<Sesion>().findAll().deleteAllFromRealm();
        }
    }
}