package com.joseluisgs.mislugares.Entidades.Lugares

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

/**
 * Clase que modela el lugar
 * @property id Long
 * @property nombre String
 * @property tipo String
 * @property fecha String
 * @property latitud String
 * @property longitud String
 * @property imagen String
 * @property favorito Boolean
 * @property votos Int
 * @property usuarioID: Long
 * @constructor
 */
@RealmClass
open class Lugar(
    @PrimaryKey
    var id: Long = 0,
    @Required
    var nombre: String = "",
    @Required
    var tipo: String = "Ciudad",
    @Required
    var fecha: String = "",
    @Required
    var latitud: String = "",
    @Required
    var longitud: String = "",
    var imagen: String = "",
    var favorito: Boolean = false,
    var votos: Int = 0,
    var usuarioID: Long = 0
): RealmObject() {
    constructor(nombre: String, tipo: String, fecha: String, latitud: String, longitud: String, imagen: String, favorito: Boolean, votos: Int, usuarioID: Long) :
            this((System.currentTimeMillis() / 1000L), nombre, tipo, fecha, latitud, longitud, imagen, favorito, votos, usuarioID)

    override fun toString(): String {
        return "Lugar(id=$id, nombre='$nombre', tipo='$tipo', fecha='$fecha', latitud='$latitud', longitud='$longitud', imagen='$imagen', favorito=$favorito, votos=$votos, usuarioID=$usuarioID)"
    }


}
