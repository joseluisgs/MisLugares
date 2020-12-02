package com.joseluisgs.mislugares.Utilidades

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream


object ImageBase64 {
    private val temp_img = "/imgages-cache"
    /**
     * Convierte una cadena Base64 a Bitmap
     *
     * @param b64String cadena Base 64
     * @return Bitmap
     */
    fun toBitmap(b64String: String): Bitmap? {
        val imageAsBytes: ByteArray = Base64.decode(b64String.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    /**
     * Convierte un Bitmap a una cadena Base64
     *
     * @param bitmap Bitmap
     * @return Cadena Base64
     */
    fun toBase64(bitmap: Bitmap): String? {
        // Comprimimos al 60 % la imagen
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        val byteArray: ByteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Devuelve la Uri de un fichero temporal (en cache) de nuestro Bitmap
     * @param bitmap Bitmap
     * @param context Context
     * @return Uri
     */
     fun fromTempUri(bitmap: Bitmap, context: Context): Uri {
         val uri = Uri.fromFile(File.createTempFile("img_", ".jpg",context.cacheDir))
         val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
         outputStream?.close()
         return uri
     }
}