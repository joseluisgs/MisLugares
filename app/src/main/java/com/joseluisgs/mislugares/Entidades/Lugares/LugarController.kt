package com.joseluisgs.mislugares.Entidades.Lugares

import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import io.realm.Realm
import io.realm.kotlin.where

/**
 * Controlador de Lugar
 */
object LugarController  {
    /**
     * Devuelve una lista de lugares
     * @return MutableList<Lugar>?
     */
    fun selectAll(): MutableList<Lugar>? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Lugar>().findAll()
        )
    }
    /**
     * Inserta un lugar
     * @param lugar Lugar
     */
    fun insert(lugar: Lugar) {
        Realm.getDefaultInstance().executeTransaction {
            it.copyToRealm(lugar); // Copia, inserta
        }
    }

    /**
     * Borra un lugar del sistema
     * @param lugar Lugar
     */
    fun delete(lugar: Lugar) {
        Realm.getDefaultInstance().executeTransaction {
            it.where<Lugar>().equalTo("id", lugar.id).findFirst()?.deleteFromRealm()
        }
    }

    /**
     * Actualiza un lugar en el sistema de almacenamiento
     */
    fun update(lugar: Lugar) {
        Realm.getDefaultInstance().executeTransaction {
            it.copyToRealmOrUpdate(lugar)
        }
    }

    /**
     * Busca lugar por nombre
     * @param login String
     * @return Lugar?
     */
    fun selectByNombre(nombre: String): Lugar? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Lugar>().equalTo("nombre", nombre).findFirst()
        )
    }

    /**
     * Busca lugares por ID
     * @param id String
     * @return Usuario?
     */
    fun selectById(id: String): Lugar? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Lugar>().equalTo("id", id).findFirst()
        )
    }

    /**
     * Elimina todos los objetos
     */
    fun removeAll() {
        Realm.getDefaultInstance().executeTransaction {
            Realm.getDefaultInstance().where<Lugar>().findAll().deleteAllFromRealm();
        }
    }
}