package com.joseluisgs.mislugares.Entidades.Sesion

import android.os.Build
import androidx.annotation.RequiresApi
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.time.Instant
import java.util.*

@RealmClass
open class Sesion (
    @PrimaryKey
    var usuarioID: String = "",
    @Required
    var time: String = "",
    @Required
    var token: String = ""
) : RealmObject()
