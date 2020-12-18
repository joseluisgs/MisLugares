package com.joseluisgs.mislugares.Entidades.Tiempo

import com.google.gson.annotations.SerializedName

class Sys {
    @SerializedName("country")
    var country: String? = null

    @SerializedName("sunrise")
    var sunrise: Long = 0

    @SerializedName("sunset")
    var sunset: Long = 0
}