package com.joseluisgs.mislugares.Entidades.Fotografias

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*

@RealmClass
open class Fotografia(
    @PrimaryKey
    var id: String = "",
    @Required
    var path: String = "",
    @Required
    var imagen: String = "",
    @Required
    var usuarioID: String = ""
) : RealmObject() {
    constructor(path: String, imagen: String, usuarioID: String) :
            this((UUID.randomUUID().toString()), imagen, path, usuarioID)

    override fun toString(): String {
        return "Fotografia(id='$id', path='$path', imagen='$imagen', usuarioID='$usuarioID')"
    }
}