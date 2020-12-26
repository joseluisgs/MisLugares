package com.joseluisgs.mislugares.Entidades.Lugares

import java.util.*

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
data class Lugar(
    // Cambiar a UUID.randomUUID().toString() o long
    var id: String = "",
    var nombre: String = "",
    var tipo: String = "Ciudad",
    var fecha: String = "",
    var latitud: String = "",
    var longitud: String = "",
    var imagenID: String = "",
    var favorito: Boolean = false,
    var votos: Int = 0,
    var time: String = "",
    var usuarioID: String = "",
){
    constructor(
        nombre: String,
        tipo: String,
        fecha: String,
        latitud: String,
        longitud: String,
        imagenID: String,
        favorito: Boolean,
        votos: Int,
        time: String,
        usuarioID: String,
    ) :
            this(
                (UUID.randomUUID().toString()),
                nombre,
                tipo,
                fecha,
                latitud,
                longitud,
                imagenID,
                favorito,
                votos,
                time,
                usuarioID
            )

    override fun toString(): String {
        return "Lugar(id='$id', nombre='$nombre', tipo='$tipo', fecha='$fecha', latitud='$latitud', longitud='$longitud', imagenID='$imagenID', favorito=$favorito, votos=$votos, time='$time', usuarioID='$usuarioID')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Lugar) return false

        if (id != other.id) return false
        if (nombre != other.nombre) return false
        if (tipo != other.tipo) return false
        if (fecha != other.fecha) return false
        if (latitud != other.latitud) return false
        if (longitud != other.longitud) return false
        if (imagenID != other.imagenID) return false
        if (favorito != other.favorito) return false
        if (votos != other.votos) return false
        if (time != other.time) return false
        if (usuarioID != other.usuarioID) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + nombre.hashCode()
        result = 31 * result + tipo.hashCode()
        result = 31 * result + fecha.hashCode()
        result = 31 * result + latitud.hashCode()
        result = 31 * result + longitud.hashCode()
        result = 31 * result + imagenID.hashCode()
        result = 31 * result + favorito.hashCode()
        result = 31 * result + votos
        result = 31 * result + time.hashCode()
        result = 31 * result + usuarioID.hashCode()
        return result
    }


}
