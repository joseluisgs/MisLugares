package com.joseluisgs.mislugares.Entidades.Fotografias

import io.realm.Realm
import io.realm.kotlin.where

object FotografiaController {
    /**
     * Devuelve una lista de fotografias
     * @return MutableList<Lugar>?
     */
    fun selectAll(): MutableList<Fotografia>? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Fotografia>().findAll()
        )
    }

    /**
     * Inserta una fotografia
     * @param fotografia Fotografia
     */
    fun insert(fotografia: Fotografia) {
        Realm.getDefaultInstance().executeTransaction {
            it.copyToRealm(fotografia); // Copia, inserta
        }
    }

    /**
     * Borra una fotografia del sistema
     * @param fotografia Fotografia
     */
    fun delete(fotografia: Fotografia) {
        Realm.getDefaultInstance().executeTransaction {
            it.where<Fotografia>().equalTo("id", fotografia.id).findFirst()?.deleteFromRealm()
        }
    }

    /**
     * Actualiza una fotografia en el sistema de almacenamiento
     */
    fun update(fotografia: Fotografia) {
        Realm.getDefaultInstance().executeTransaction {
            it.copyToRealmOrUpdate(fotografia)
        }
    }

    /**
     * Busca fotografia por ID
     * @param id String
     * @return Fotografia
     */
    fun selectById(id: String): Fotografia? {
        return Realm.getDefaultInstance().copyFromRealm(
            Realm.getDefaultInstance().where<Fotografia>().equalTo("id", id).findFirst()
        )
    }

    /**
     * Elimina todos los objetos
     */
    fun removeAll() {
        Realm.getDefaultInstance().executeTransaction {
            Realm.getDefaultInstance().where<Fotografia>().findAll().deleteAllFromRealm();
        }
    }
}