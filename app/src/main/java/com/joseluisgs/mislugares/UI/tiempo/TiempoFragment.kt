package com.joseluisgs.mislugares.UI.tiempo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.joseluisgs.mislugares.Entidades.Tiempo.WeatherResponse
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.MisLugaresAPI
import com.joseluisgs.mislugares.Services.Tiempo.ElTiempoAPI
import kotlinx.android.synthetic.main.fragment_brujula.*
import kotlinx.android.synthetic.main.fragment_tiempo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TiempoFragment: Fragment() {
    private lateinit var mPosicion: FusedLocationProviderClient
    private lateinit var localizacion: Location
    private lateinit var posicion: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tiempo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPosicion = LocationServices.getFusedLocationProviderClient(activity!!)
        // Evento que obtiene la posición
        mPosicion.lastLocation
            .addOnSuccessListener { location : Location? ->
               this.localizacion = location!!
                Log.i("Tiempo", this.localizacion.toString())
                obtenerInformacionTiempo()
            }
    }

    /**
     * Obtiene información del Tiempo
     */
    private fun obtenerInformacionTiempo() {
        val clientREST = ElTiempoAPI.service
        val call = clientREST.getCurrentWeatherData(this.localizacion.latitude.toString(), this.localizacion.longitude.toString(),
            ElTiempoAPI.API_KEY, ElTiempoAPI.UNITS, ElTiempoAPI.LANG)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!

                    val stringBuilder = "Country: " +
                            weatherResponse.sys!!.country +
                            "\n" +
                            "Temperature: " +
                            weatherResponse.main!!.temp +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main!!.temp_min +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main!!.temp_max +
                            "\n" +
                            "Humidity: " +
                            weatherResponse.main!!.humidity +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main!!.pressure

                    tiempoText!!.text = stringBuilder
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                tiempoText!!.text = t.message
            }
        })
    }

}