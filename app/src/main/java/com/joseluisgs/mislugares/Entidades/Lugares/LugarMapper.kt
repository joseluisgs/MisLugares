package com.joseluisgs.mislugares.Entidades.Lugares

import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.Entidades.Usuarios.UsuarioDTO

/**
 * Mapea entre DTO y Clase Modelo
 */
object LugarMapper {
    /**
     * Una lista de DTO a Modelos
     * @param items List<LugarDTO>
     * @return List<Lugar>
     */
    fun fromDTO(items: List<LugarDTO>): List<Lugar> {
        return items.map { fromDTO(it) }
    }

    /**
     * Una lista de Modelos a DTO
     * @param items List<Lugar>
     * @return List<LugarDTO>
     */
    fun toDTO(items: List<Lugar>): List<LugarDTO> {
        return items.map { toDTO(it) }
    }

    /**
     * DTO a Modelo
     * @param dto LugarDTO
     * @return Lugar
     */
    fun fromDTO(dto: LugarDTO): Lugar {
        return Lugar(
            dto.id,
            dto.nombre,
            dto.tipo,
            dto.fecha,
            dto.latitud,
            dto.longitud,
            dto.imagenID,
            dto.favorito,
            dto.votos,
            dto.time,
            dto.usuarioID
        )
    }

    /**
     * Modelo a DTO
     * @param model Lugar
     * @return LugarDTO
     */
    fun toDTO(model: Lugar): LugarDTO {
        return LugarDTO(
            model.id,
            model.nombre,
            model.tipo,
            model.fecha,
            model.latitud,
            model.longitud,
            model.imagenID,
            model.favorito,
            model.votos,
            model.time,
            model.usuarioID
        )
    }
}