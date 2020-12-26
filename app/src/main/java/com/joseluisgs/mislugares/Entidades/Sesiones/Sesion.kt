package com.joseluisgs.mislugares.Entidades.Sesiones

import java.io.Serializable

open class Sesion(
    var usuarioID: String = "",
    var time: String = "",
    var token: String = "",
) {
    fun fromSesion(sesion: Sesion) {
        this.usuarioID = sesion.usuarioID
        this.time = sesion.time
        this.token = sesion.token
    }
}
