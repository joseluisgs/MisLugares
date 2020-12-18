package com.joseluisgs.mislugares.Entidades.Fotografias

import com.google.gson.annotations.SerializedName

/**
 * Calse trasformadora de Fotografía
 * @property id String
 * @property imagen String
 * @property uri String
 * @property hash String
 * @property time String
 * @property usuarioID String
 * @constructor
 */
class FotografiaDTO(
    @SerializedName("id") val id: String,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("hash") val hash: String,
    @SerializedName("time") val time: String,
    @SerializedName("usuarioID") val usuarioID: String,
)