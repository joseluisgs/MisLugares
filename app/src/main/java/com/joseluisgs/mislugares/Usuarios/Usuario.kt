package com.joseluisgs.mislugares.Usuarios

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

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
open class Usuario(
    // Es importante iniciar todos los valores de la clases
    // Ponemos los datos que queremos almacenar
    @PrimaryKey var id: Long = 0,
    var nombre: String ="",
    var login: String ="",
    var password: String ="",
    var avatar: String ="",
    var correo: String ="",
    var twitter: String ="",
    var github: String ="") : RealmObject() {
        constructor(nombre: String, login: String, password: String, avatar: String, correo: String, twitter: String, github: String) :
                this((System.currentTimeMillis() / 1000L), nombre, login, password, avatar, correo, twitter, github)
}