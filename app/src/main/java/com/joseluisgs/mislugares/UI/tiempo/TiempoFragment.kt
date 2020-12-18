package com.joseluisgs.mislugares.UI.tiempo

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.joseluisgs.mislugares.Entidades.Tiempo.WeatherResponse
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.Tiempo.ElTiempoAPI
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_tiempo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

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

                    tiempoCiudad.text = weatherResponse.name
                    tiempoPais.text = weatherResponse.sys?.country
                    var sdf = SimpleDateFormat("HH:mm   dd/MM/yyyy")
                    var date = Date((weatherResponse.dt.toLong() * 1000))
                    tiempoHora.text =  sdf.format(date)
                    tiempoTemperatura.text = weatherResponse.main?.temp.toString() + "º"
                    Picasso.get()
                        // .load(R.drawable.user_avatar)
                        .load("http://openweathermap.org/img/wn/"+weatherResponse.weather[0].icon+"@2x.png")
                        .resize(200, 200)
                        .into(tiempoImagen)
                    tiempoDescripcion.text = weatherResponse.weather[0].description.toString().capitalize()
                    tiempoTempMax.text = "Temperatura (Max): " + weatherResponse.main?.temp_max + "º"
                    tiempoTempMin.text = "Temperatura (Min): " + weatherResponse.main?.temp_min + "º"
                    tiempoHumedad.text = "Humedad: " + weatherResponse.main?.humidity + " %"
                    tiempoPresion.text = "Presión: " + weatherResponse.main?.pressure + " mBar"
                    tiempoVisibilidad.text = "Visibilidad: " + weatherResponse.visibility + " m"
                    sdf = SimpleDateFormat("HH:mm")
                    date = Date((weatherResponse.sys!!.sunrise * 1000))
                    tiempoAmanecerHora.text = sdf.format(date)
                    date = Date((weatherResponse.sys!!.sunset * 1000))
                    tiempoAnocherHora.text = sdf.format(date)
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

}