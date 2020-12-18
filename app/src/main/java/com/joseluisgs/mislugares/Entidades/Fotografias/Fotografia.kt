package com.joseluisgs.mislugares.Entidades.Fotografias

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.io.Serializable
import java.util.*

@RealmClass
open class Fotografia(
    @PrimaryKey
    var id: String = "",
    @Required
    var imagen: String = "",
    @Required
    var uri: String = "",
    @Required
    var hash: String = "",
    @Required
    var time: String = "",
    @Required
    var usuarioID: String = "",
) : RealmObject(), Serializable {
    constructor(imagen: String, uri: String, hash: String, time: String, usuarioID: String) :
            this((UUID.randomUUID().toString()), imagen, uri, hash, time, usuarioID)


}