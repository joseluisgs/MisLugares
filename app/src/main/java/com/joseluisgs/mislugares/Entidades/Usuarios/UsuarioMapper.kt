package com.joseluisgs.mislugares.Entidades.Usuarios

/**
 * Mapea entre DTO y Clase Modelo
 */
object UsuarioMapper {
    /**
     * Una lista de DTO a Modelos
     * @param items List<UsuarioDTO>
     * @return List<Usuario>
     */
    fun fromDTO(items: List<UsuarioDTO>): List<Usuario> {
        return items.map { fromDTO(it) }
    }

    /**
     * Una lista de Modelos a DTO
     * @param items List<Usuario>
     * @return List<UsuarioDTO>
     */
    fun toDTO(items: List<Usuario>): List<UsuarioDTO> {
        return items.map { toDTO(it) }
    }

    /**
     * DTO a Modelo
     * @param dto UsuarioDTO
     * @return Usuario
     */
    fun fromDTO(dto: UsuarioDTO): Usuario {
        return Usuario(
            dto.id,
            dto.nombre,
            dto.login,
            dto.password,
            dto.avatar,
            dto.correo,
            dto.twitter,
            dto.github
        )
    }

    /**
     * Modelo a DTO
     * @param model Usuario
     * @return UsuarioDTO
     */
    fun toDTO(model: Usuario): UsuarioDTO {
        return UsuarioDTO(
            model.id,
            model.nombre,
            model.login,
            model.password,
            model.avatar,
            model.correo,
            model.twitter,
            model.github
        )
    }
}