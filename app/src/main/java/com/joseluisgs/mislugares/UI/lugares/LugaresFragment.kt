package com.joseluisgs.mislugares.UI.lugares

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.R

class LugaresFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lugares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Lugares", "Creando Lista Lugares")

        // Vamos a probar si hemos insertado bien
        val lugares = LugarController.selectAll()
        Log.i("Lugares", lugares?.size.toString())
        val lugar = lugares?.get(0)
        Log.i("Lugares", lugar.toString())
    }
}