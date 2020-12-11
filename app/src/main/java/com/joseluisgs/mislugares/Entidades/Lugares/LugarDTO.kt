package com.joseluisgs.mislugares.Entidades.Lugares

import com.google.gson.annotations.SerializedName

/**
 * Clase trasformadora de Lugar
 * @property id String
 * @property nombre String
 * @property tipo String
 * @property fecha String
 * @property latitud String
 * @property longitud String
 * @property imagenID String
 * @property favorito Boolean
 * @property votos Int
 * @property time String
 * @property usuarioID String
 * @constructor
 */
class LugarDTO(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("latitud") val latitud: String,
    @SerializedName("longitud") val longitud: String,
    @SerializedName("imagenID") val imagenID: String,
    @SerializedName("favorito") val favorito: Boolean,
    @SerializedName("votos") val votos: Int,
    @SerializedName("time") val time: String,
    @SerializedName("usuarioID") val usuarioID: String,
)