package com.joseluisgs.mislugares.Usuarios

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

/**
 * Clase Modelo de Usuario
 * @property id Long
 * @property nombre String
 * @property login String
 * @property password String
 * @property avatar String
 * @property correo String
 * @property twitter String
 * @property github String
 * @constructor
 */
@RealmClass
open class Usuario(
    // Es importante iniciar todos los valores de la clases
    // Ponemos los datos que queremos almacenar
    @PrimaryKey
    var id: Long = 0,
    @Required
    var nombre: String ="",
    @Required @Index
    var login: String ="",
    @Required
    var password: String ="",
    var avatar: String ="",
    @Required @Index
    var correo: String ="",
    var twitter: String ="",
    var github: String =""
) : RealmObject() {

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
    constructor(nombre: String, login: String, password: String, avatar: String, correo: String, twitter: String, github: String) :
                this((System.currentTimeMillis() / 1000L), nombre, login, password, avatar, correo, twitter, github)

    override fun toString(): String {
        return "Usuario(id=$id, nombre='$nombre', login='$login', password='$password', avatar='$avatar', correo='$correo', twitter='$twitter', github='$github')"
    }


}