package com.joseluisgs.mislugares.Entidades.Fotografias

import java.util.*

data class Fotografia(
    var id: String = "",
    var imagen: String = "",
    var uri: String = "",
    var hash: String = "",
    var time: String = "",
    var usuarioID: String = "",
) {
    constructor(imagen: String, uri: String, hash: String, time: String, usuarioID: String) :
            this((UUID.randomUUID().toString()), imagen, uri, hash, time, usuarioID)


}