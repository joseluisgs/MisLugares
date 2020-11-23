package Utilidades

import java.security.MessageDigest
import kotlin.experimental.and


/**
 * Singleton de Cifrador
 */
object Cifrador {
    // tipo por ejemplo: "SHA-256"
    fun toHash(cadena: String, tipo: String): String? {
        var md: MessageDigest? = null
        var hash: ByteArray? = null
        // Llamamos a la función de hash
        try {
            md = MessageDigest.getInstance(tipo)
            hash = md.digest(cadena.toByteArray(charset("UTF-8")))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return convertToHex(hash)
    }

    /**
     * Converts the given byte[] to a hex string.
     *
     * @param raw the byte[] to convert
     * @return the string the given byte[] represents
     */
    private fun convertToHex(raw: ByteArray?): String? {
        val sb = StringBuffer()
        for (i in raw!!.indices) {
            sb.append(((raw[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
        }
        return sb.toString()
    }
}