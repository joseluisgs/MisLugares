package com.joseluisgs.mislugares.Entidades.Sesiones

import com.google.gson.annotations.SerializedName

/**
 * Traformador de Sesiones
 * @property id String
 * @property time String
 * @property token String
 * @constructor
 */
class SesionDTO(
    @SerializedName("id") val id: String,
    @SerializedName("time") val time: String,
    @SerializedName("token") val token: String,
)