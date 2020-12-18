package com.joseluisgs.mislugares.UI.brujula

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_brujula.*


class BrujulaFragment : Fragment(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    var giroscopio: Sensor? = null
    var acelerometro: Sensor? = null
    var mGravity: FloatArray? = null
    var mGeomagnetic: FloatArray? = null
    var azimut: Float = 0.toFloat()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_brujula, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager?
        acelerometro = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        giroscopio = sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (giroscopio != null) {
            sensorManager!!.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager!!.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> mGravity = event.values
                Sensor.TYPE_MAGNETIC_FIELD -> mGeomagnetic = event.values
                //Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> mGeomagnetic = event.values
            }
        }
        if (mGravity != null && mGeomagnetic != null) {
            val RotationMatrix = FloatArray(16)
            val success = SensorManager.getRotationMatrix(RotationMatrix, null, mGravity, mGeomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(RotationMatrix, orientation)
                azimut = orientation[0] * (180 / Math.PI.toFloat())
            }
        }
        //try {
        val grados = azimut * -1
        brujulaImagen.rotation = grados
        brujulaTexto.text = "Grados: $grados º"
        //}catch (ex: Exception) {

        //}
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    /**
     * Para que no se quede el sensor leyendo
     */
    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }


}