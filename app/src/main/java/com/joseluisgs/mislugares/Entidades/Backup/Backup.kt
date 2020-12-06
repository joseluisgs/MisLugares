package com.joseluisgs.mislugares.Entidades.Backup

import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario

/**
 * La clase Backup tiene la estructura de del fichero de JSON que quiero conseguir
 */
class Backup (
    val usuarios: MutableList<Usuario>,
    val lugares: MutableList<Lugar>,
    val fotografias: MutableList<Fotografia>
) {


}