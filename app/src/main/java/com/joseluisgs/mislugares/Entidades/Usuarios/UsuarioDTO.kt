package com.joseluisgs.mislugares.Entidades.Usuarios

import com.google.gson.annotations.SerializedName

/**
 * Transformador de objetos de la clase usuario
 * @property id String
 * @property nombre String
 * @property login String
 * @property password String
 * @property avatar String
 * @property correo String
 * @property twitter String
 * @property github String
 * @constructor
 */
class UsuarioDTO(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("twitter") val twitter: String,
    @SerializedName("github") val github: String
)

