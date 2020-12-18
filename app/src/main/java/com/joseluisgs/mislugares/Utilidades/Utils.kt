package com.joseluisgs.mislugares.Utilidades

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import java.io.File

object Utils {
    /**
     * Manda un correo
     * @param activity [ERROR : FragmentActivity]
     */
    fun mandarEMail(
        activity: FragmentActivity?,
        para: String = "",
        cc: String = "",
        asunto: String = "",
        texto: String = "",
    ) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"
        // Los receptores deben ser un array, ya sean uno o varios, por eso los casteamos
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(para))
        intent.putExtra(Intent.EXTRA_CC, cc)
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto)
        intent.putExtra(Intent.EXTRA_TEXT, texto)
        try {
            activity?.startActivity(Intent.createChooser(intent, "Enviar usando..."))
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Abre una dirección en en navegador
     * @param activity FragmentActivity?
     * @param url String
     */
    fun abrirURL(activity: FragmentActivity, url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        activity.startActivity(intent)
    }

    /**
     * Comprueba si está conectado a internet por algún medio
     * @param context Context?
     * @return Boolean
     */
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                Log.i("Internet", "ActiveNetworkInfo.isConnected")
                return true
            }
        }
        Log.i("Internet", "Sin red")
        return false
    }

    /**
     * Comprueba si esta el GPS Activo
     * @param context Context?
     * @return Boolean
     */
    fun isGPSAvaliable(context: Context?): Boolean {
        val locationManager = context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (gpsStatus) {
            Log.i("GPS", "GPS Activado")
            true
        } else {
            Log.i("GPS", "GPS Desactivado")
            false
        }
    }

    /**
     * Elimina los ficheros de cache que hayamos creado al cerrar la apliación
     * @param context Context
     */
    fun deleteCache(context: Context) {
        val cacheDir: File = context.cacheDir
        val files: Array<File> = cacheDir.listFiles()!!
        for (file in files)
            if (file.isFile)
                file.delete()
    }
}