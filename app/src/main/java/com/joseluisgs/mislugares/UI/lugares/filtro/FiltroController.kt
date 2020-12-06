package com.joseluisgs.mislugares.UI.lugares.filtro

/**
 * Filtros
 */
object FiltroController {
    /**
     * Analiza el filtro de una secuencia de voz
     * @param secuencia String
     * @return Filtro
     */
    fun analizarFiltroSecuencia(secuencia: String): Filtro {
        var FILTRO = Filtro.NADA
        if (secuencia.contains("nombre") &&
            !(secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.NOMBRE_ASC
        } else if (secuencia.contains("nombre") &&
            (secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.NOMBRE_DESC

        }

        // Fecha
        else if (secuencia.contains("fecha") &&
            !(secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.FECHA_ASC
        } else if (secuencia.contains("fecha") &&
            (secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.FECHA_DESC
        }

        // Tipo
        else if (secuencia.contains("tipo") &&
            !(secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.TIPO_ASC
        } else if (secuencia.contains("tipo") &&
            (secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.TIPO_DESC
        }

        // Favorito
        else if (secuencia.contains("favorito") &&
            !(secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.FAVORITO_ASC
        } else if (secuencia.contains("favorito") &&
            (secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.FAVORITO_DESC
        }

        // Votos
        else if (secuencia.contains("votos") &&
            !(secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.VOTOS_ASC
        } else if (secuencia.contains("votos") &&
            (secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.VOTOS_DESC
        }

        // Lugar
        else if (secuencia.contains("lugar") &&
            !(secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.NOMBRE_ASC
        } else if (secuencia.contains("lugar") &&
            (secuencia.contains("descendente") || secuencia.contains("inverso"))
        ) {
            FILTRO = Filtro.NOMBRE_DESC
        }
        // Por defecto
        else {
            FILTRO = Filtro.NADA
        }
        return FILTRO
    }

    /**
     * Devuelve el filtro de un lista de opciones
     * @param position Int
     * @return Filtro
     */
    fun analizarFiltroSpinner(position: Int): Filtro {
        return when (position) {
            0 -> Filtro.NADA
            1 -> Filtro.NOMBRE_ASC
            2 -> Filtro.NOMBRE_DESC
            3 -> Filtro.FECHA_ASC
            4 -> Filtro.FECHA_DESC
            5 -> Filtro.TIPO_ASC
            6 -> Filtro.TIPO_DESC
            7 -> Filtro.FAVORITO_ASC
            8 -> Filtro.FAVORITO_DESC
            9 -> Filtro.VOTOS_ASC
            10 -> Filtro.VOTOS_DESC
            else -> Filtro.NADA
        }
    }
}