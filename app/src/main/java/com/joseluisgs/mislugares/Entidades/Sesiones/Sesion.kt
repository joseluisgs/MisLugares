package com.joseluisgs.mislugares.Entidades.Sesiones

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class Sesion (
    @PrimaryKey
    var usuarioID: String = "",
    @Required
    var time: String = "",
    @Required
    var token: String = ""
) : RealmObject()
