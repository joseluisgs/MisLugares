package com.joseluisgs.mislugares.Entidades.Sesiones

/**
 * Mapea entre DTO y Clase Modelo
 */
object SesionMapper {
    /**
     * Una lista de DTO a Modelos
     * @param items List<SesionDTO>
     * @return List<Sesion>
     */
    fun fromDTO(items: List<SesionDTO>): List<Sesion> {
        return items.map { fromDTO(it) }
    }

    /**
     * Una lista de Modelos a DTO
     * @param items List<Sesion>
     * @return List<SesionDTO>
     */
    fun toDTO(items: List<Sesion>): List<SesionDTO> {
        return items.map { toDTO(it) }
    }

    /**
     * DTO a Modelo
     * @param dto SesionDTO
     * @return Sesion
     */
    fun fromDTO(dto: SesionDTO): Sesion {
        return Sesion(
            dto.id,
            dto.time,
            dto.token
        )
    }

    /**
     * Modelo a DTO
     * @param model Sesion
     * @return SesionDTO
     */
    fun toDTO(model: Sesion): SesionDTO {
        return SesionDTO(
            model.usuarioID,
            model.time,
            model.token
        )
    }
}