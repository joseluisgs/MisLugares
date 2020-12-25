package com.joseluisgs.mislugares.Entidades.Usuarios

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.io.Serializable
import java.util.*

/**
 * Clase Modelo de Usuario
 * @property id String
 * @property nombre String
 * @property login String
 * @property password String
 * @property avatar String
 * @property correo String
 * @property twitter String
 * @property github String
 * @constructor
 */
data class Usuario(
    // Es importante iniciar todos los valores de la clases
    // Ponemos los datos que queremos almacenar
    // Cambiar a UUID.randomUUID().toString() o long
    var id: String = "",
    var nombre: String = "",
    var login: String = "",
    var password: String = "",
    var avatar: String = "",
    var correo: String = "",
    var twitter: String = "",
    var github: String = "",
) {

    /**
     * Constructor
     * @param nombre String
     * @param login String
     * @param password String
     * @param avatar String
     * @param correo String
     * @param twitter String
     * @param github String
     * @constructor
     */
    constructor(
        nombre: String,
        login: String,
        password: String,
        avatar: String,
        correo: String,
        twitter: String,
        github: String,
    ) :
            this((UUID.randomUUID().toString()), nombre, login, password, avatar, correo, twitter, github)
    // this((System.currentTimeMillis() / 1000L), nombre, login, password, avatar, correo, twitter, github)

    override fun toString(): String {
        return "Usuario(id=$id, nombre='$nombre', login='$login', password='$password', avatar='$avatar', correo='$correo', twitter='$twitter', github='$github')"
    }
}