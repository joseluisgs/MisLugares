package com.joseluisgs.mislugares.Entidades.Backup

import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Sesiones.Sesion
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario

class Backup(
    val usuarios: MutableList<Usuario>,
    val lugares: MutableList<Lugar>,
    val fotografias: MutableList<Fotografia>,
    val sesiones: MutableList<Sesion> // No es necesario
    )
