package com.joseluisgs.mislugares.UI.lugares

import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_lugares.*

class LugaresFragment : Fragment() {
    // Propiedades
    private var LUGARES = mutableListOf<Lugar>()
    private lateinit var lugaresAdapter: LugaresListAdapter //Adaptador de Noticias de Recycler
    private lateinit var tareaLugares: TareaCargarLugares // Tarea en segundo plano
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
        // Iniciamos la interfaz
        initUI()

//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//           Log.i("Lugares", "Pulsado Atras")
//        }


    }

    /**
     * Inicia la Interfaz de Usuario
     */
    private fun initUI() {
        Log.i("Lugares", "Init IU")
        iniciarSwipeRecarga()
        cargarLugares()
        iniciarSwipeHorizontal()
        lugaresRecycler.layoutManager = LinearLayoutManager(context)
        lugaresFabNuevo.setOnClickListener { nuevoElemento() }
        Log.i("Lugares", "Fin la IU")
    }

    /**
     * Deslizamiento vertical para recargar
     */
    private fun iniciarSwipeRecarga() {
        lugaresSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark)
        lugaresSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorAccent)
        lugaresSwipeRefresh.setOnRefreshListener {
            cargarLugares()
        }
    }

    /**
     * Realiza el swipe horizontal si es necesario
     */
    private fun iniciarSwipeHorizontal() {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or
                    ItemTouchHelper.RIGHT
        ) {
            // Sobreescribimos los métodos
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Analizamos el evento según la dirección
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // Si pulsamos a la de izquierda o a la derecha
                // Programamos la accion
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Log.d("Noticias", "Tocado izquierda");
                        borrarElemento(position)
                    }
                    else -> {
                        //  Log.d("Noticias", "Tocado derecha");
                        editarElemento(position)
                    }
                }
            }

            // Dibujamos los botones y eveneto. Nos lo creemos :):)
            // IMPORTANTE
            // Para que no te explote las imagenes deben ser PNG
            // Así que añade un IMAGE ASEET bjándtelos de internet
            // https://material.io/resources/icons/?style=baseline
            // como PNG y cargas el de mayor calidad
            // de otra forma Bitmap no funciona bien
            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3
                    // Si es dirección a la derecha: izquierda->derecta
                    // Pintamos de azul y ponemos el icono
                    if (dX > 0) {
                        // Pintamos el botón izquierdo
                        botonIzquierdo(canvas, dX, itemView, width)
                    } else {
                        // Caso contrario
                        botonDerecho(canvas, dX, itemView, width)
                    }
                }
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        // Añadimos los eventos al RV
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(lugaresRecycler)
    }

    /**
     * Mostramos el elemento iquierdo
     * @param canvas Canvas
     * @param dX Float
     * @param itemView View
     * @param width Float
     */
    private fun botonDerecho(canvas: Canvas, dX: Float, itemView: View, width: Float) {
        // Pintamos de rojo y ponemos el icono
        paintSweep.color = Color.RED
        val background = RectF(
            itemView.right.toFloat() + dX,
            itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat()
        )
        canvas.drawRect(background, paintSweep)
        val icon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_seep_eliminar)
        val iconDest = RectF(
            itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right
                .toFloat() - width, itemView.bottom.toFloat() - width
        )
        canvas.drawBitmap(icon, null, iconDest, paintSweep)
    }

    /**
     * Mostramos el elemento izquierdo
     * @param canvas Canvas
     * @param dX Float
     * @param itemView View
     * @param width Float
     */
    private fun botonIzquierdo(canvas: Canvas, dX: Float, itemView: View, width: Float) {
        // Pintamos de azul y ponemos el icono
        paintSweep.setColor(Color.BLUE)
        val background = RectF(
            itemView.left.toFloat(), itemView.top.toFloat(), dX,
            itemView.bottom.toFloat()
        )
        canvas.drawRect(background, paintSweep)
        val icon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_sweep_editar)
        val iconDest = RectF(
            itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left
                .toFloat() + 2 * width, itemView.bottom.toFloat() - width
        )
        canvas.drawBitmap(icon, null, iconDest, paintSweep)
    }

    /**
     * Carga los lugares
     */
    fun cargarLugares() {
        tareaLugares = TareaCargarLugares()
        tareaLugares.execute()
    }

    /**
     * Abre un nuevo elemeneto
     */
    private fun nuevoElemento() {
        Log.i("Lugares", "Nuevo lugar")
        abrirDetalle(null, Modo.INSERTAR, this, null)
    }

    /**
     * Inserta un elemento en la lista
     */
    fun insertarItemLista(item: Lugar) {
        this.lugaresAdapter.addItem(item)
        lugaresAdapter.notifyDataSetChanged()
        // Si queremos forzar la recarga
        // cargarLugares()
    }

    /**
     * Edita el elemento en la posición seleccionada
     * @param position Int
     */
    private fun editarElemento(position: Int) {
        Log.i("Lugares", "Editando el elemento pos: " + position)
        abrirDetalle(LUGARES[position], Modo.ACTUALIZAR, this, position)
    }

    fun actualizarItemLista(item: Lugar, position: Int) {
        this.lugaresAdapter.updateItem(item,position)
        lugaresAdapter.notifyDataSetChanged()
        // Si queremos forzar la recarga
        // cargarLugares()
    }

    /**
     * Borra el elemento en la posición seleccionada
     * @param position Int
     */
    private fun borrarElemento(position: Int) {
        Log.i("Lugares", "Borrando el elemento pos: " + position)
        abrirDetalle(LUGARES[position], Modo.ELIMINAR, this, position)
    }

    /**
     * Elimina un elemento de la vista
     * @param position Int
     */
    fun eliminarItemLista(position: Int) {
        this.lugaresAdapter.removeItem(position)
        lugaresAdapter.notifyDataSetChanged()
        // Si queremos forzar la recarga
        // cargarLugares()
    }

    fun actualizarVistaLista() {
        lugaresRecycler.adapter = lugaresAdapter
    }

    /**
     * Abre el elemento en la posición didicada
     * @param lugar Lugar
     */
    private fun abrirElemento(lugar: Lugar) {
        Log.i("Lugares", "Visualizando el elemento: " + lugar.id)
        abrirDetalle(lugar, Modo.VISUALIZAR, this, null)
    }

    private fun abrirDetalle(lugar: Lugar?, modo: Modo?, anterior: LugaresFragment?, position: Int?) {
        Log.i("Lugares", "Abriendo el elemento pos: " + lugar?.id)
        val lugarDetalle = LugarDetalleFragment(lugar, modo, anterior, position)
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        // animaciones
        // transaction.setCustomAnimations(R.anim.animacion_fragment1, R.anim.animacion_fragment2)
        //Llamamos al replace/ add
        transaction.replace(R.id.nav_host_fragment, lugarDetalle)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Evento clic asociado a una fila
     * @param lugar Lugar
     */
    private fun eventoClicFila(lugar: Lugar) {
            abrirElemento(lugar)
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
            lugaresAdapter = LugaresListAdapter(LUGARES) {
                eventoClicFila(it)
            }

            lugaresRecycler.adapter = lugaresAdapter
            // Avismos que ha cambiado
            lugaresAdapter.notifyDataSetChanged()
            lugaresRecycler.setHasFixedSize(true)
            lugaresSwipeRefresh.isRefreshing = false
            Toast.makeText(context, "Lugares cargados", Toast.LENGTH_LONG).show()
        }

    }


    override fun onStop() {
        super.onStop()
        tareaLugares.cancel(true)
    }
}

