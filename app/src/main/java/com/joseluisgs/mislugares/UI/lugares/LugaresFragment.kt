package com.joseluisgs.mislugares.UI.lugares

import android.graphics.Paint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_lugares.*

class LugaresFragment : Fragment() {
    // Propiedades
    private var LUGARES = mutableListOf<Lugar>()
    private lateinit var adapter: LugaresListAdapter //Adaptador de Noticias de Recycler
    private lateinit var tarea: TareaCargarLugares // Tarea en segundo plano
    private var paintSweep = Paint()


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
        // Iniciamos la interfaz
        initUI()


    }

    private fun initUI() {
        Log.i("Lugares", "Iniciando la IU")
        iniciarSwipeRecarga()
        cargarLugares()

        lugaresRecycler.layoutManager = LinearLayoutManager(context);

    }

    private fun iniciarSwipeRecarga() {
        lugaresSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark)
        lugaresSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent)
        lugaresSwipeRefresh.setOnRefreshListener {
            cargarLugares()
        }
    }

    /**
     * Carga los lugares
     */
    private fun cargarLugares() {
        tarea = TareaCargarLugares()
        tarea.execute()
    }


    /**
     * Evento clic asociado a una fila
     * @param lugar Lugar
     */
    private fun eventoClicFila(lugar: Lugar) {
        Log.i("Lugares", "Has hecho clic en el Lugar: $lugar")
        // abrirNoticia(noticia)
    }

    /**
     * Tarea asíncrona para la carga de noticias
     */
    inner class TareaCargarLugares : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            if (lugaresSwipeRefresh.isRefreshing) {
                lugaresSwipeRefresh.isRefreshing = false
            }
            Toast.makeText(context, "Obteniendo lugares", Toast.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg args: Void?): Void? {
            try {
                LUGARES = LugarController.selectAll()!!
                Log.i("Lugares", "Lista de lugares de tamaño: " + LUGARES.size)
            } catch (e: Exception) {
                Log.e("T2Plano ", e.message.toString());
            }
            return null
        }

        override fun onPostExecute(args: Void?) {
            adapter = LugaresListAdapter(LUGARES) {
                eventoClicFila(it)
            }

            lugaresRecycler.adapter = adapter
            // Avismos que ha cambiado
            adapter.notifyDataSetChanged()
            lugaresRecycler.setHasFixedSize(true)
            lugaresSwipeRefresh.isRefreshing = false
            Toast.makeText(context, "Lugares cargados", Toast.LENGTH_LONG).show()
        }

    }
}

