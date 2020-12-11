package com.joseluisgs.mislugares.Entidades.Fotografias


/**
 * Mapea entre DTO y Clase Modelo
 */
object FotografiaMapper {
    /**
     * Una lista de DTO a Modelos
     * @param items List<FotografiaDTO>
     * @return List<Fotografia>
     */
    fun fromDTO(items: List<FotografiaDTO>): List<Fotografia> {
        return items.map { fromDTO(it) }
    }

    /**
     * Una lista de Modelos a DTO
     * @param items List<Fotografia>
     * @return List<FotografiaDTO>
     */
    fun toDTO(items: List<Fotografia>): List<FotografiaDTO> {
        return items.map { toDTO(it) }
    }

    /**
     * DTO a Modelo
     * @param dto FotografiaDTO
     * @return Fotografia
     */
    fun fromDTO(dto: FotografiaDTO): Fotografia {
        return Fotografia(
            dto.id,
            dto.imagen,
            dto.uri,
            dto.hash,
            dto.time,
            dto.usuarioID
        )
    }

    /**
     * Modelo a DTO
     * @param model Fotografia
     * @return FotografiaDTO
     */
    fun toDTO(model: Fotografia): FotografiaDTO {
        return FotografiaDTO(
            model.id,
            model.imagen,
            model.uri,
            model.hash,
            model.time,
            model.usuarioID
        )
    }
}