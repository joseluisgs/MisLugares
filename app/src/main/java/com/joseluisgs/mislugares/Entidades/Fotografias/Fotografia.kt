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
    var nombre: String = "",
    @Required
    var imagen: String = "",
    @Required
    var path: String = "",
    @Required
    var usuarioID: String = ""
) : RealmObject() {
    constructor(nombre: String, imagen: String, path: String, usuarioID: String) :
            this((UUID.randomUUID().toString()), nombre, imagen, path, usuarioID)

    override fun toString(): String {
        return "Fotografia(id='$id', path='$path', imagen='$imagen', usuarioID='$usuarioID')"
    }
}