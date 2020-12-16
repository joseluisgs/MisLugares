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
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_brujula.*

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
            }
    }

}