package com.joseluisgs.mislugares.Entidades.Backup

import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario

class Backup(
    val usuario: Usuario,
    val lugares: MutableList<Lugar>,
    val fotografias: MutableList<Fotografia>,
) {
    override fun toString(): String {
        return "Backup(usuario=$usuario, lugares=$lugares, fotografias=$fotografias)"
    }
}


