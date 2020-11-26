package com.joseluisgs.mislugares.Usuarios

import io.realm.Realm
import io.realm.Realm.*
import io.realm.kotlin.where

/**
 * Controlador de Usuarios
 */
object UsuarioController {
    /**
     * Inserta un usuario
     * @param usuario Usuario
     */
    fun insert(usuario: Usuario) {
        getDefaultInstance().executeTransaction {
            it.copyToRealm(usuario); // Copia, inserta
        }
    }

    /**
     * Borra un usuario del sistema
     * @param usuario Usuario
     */
    fun delete(usuario: Usuario) {
        getDefaultInstance().executeTransaction {
            it.where<Usuario>().equalTo("id", usuario.id).findFirst()?.deleteFromRealm()
        }
    }

    /**
     * Actualiza un usuario en el sistema de almacenamiento
     */
    fun update(usuario: Usuario) {
        getDefaultInstance().executeTransaction {
            it.copyToRealmOrUpdate(usuario)
        }
    }

    /**
     * Busca usuario por Login
     * @param login String
     * @return Usuario?
     */
    fun selectByLogin(login: String): Usuario? {
        return getDefaultInstance().copyFromRealm(
            getDefaultInstance().where<Usuario>().equalTo("login", login).findFirst()
        )
    }

    /**
     * Busca usuarios por ID
     * @param id Long
     * @return Usuario?
     */
    fun selectById(id: Long): Usuario? {
        return getDefaultInstance().copyFromRealm(
            getDefaultInstance().where<Usuario>().equalTo("id", id).findFirst()
        )
    }

    /**
     * Elimina todos los objetos
     */
    fun removeAll() {
        getDefaultInstance().executeTransaction {
            getDefaultInstance().where<Usuario>().findAll().deleteAllFromRealm();
        }
    }
}