package com.joseluisgs.mislugares.UI.lugares

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaController
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.item_lugar.view.*


class LugaresListAdapter(
    private val listaLugares: MutableList<Lugar>,
    // Famos a tener distintas acciones y eventos
    private val accionPrincipal: (Lugar) -> Unit

) : RecyclerView.Adapter<LugaresListAdapter.LugarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        return LugarViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lugar, parent, false)
        )
    }

    /**
     * Procesamos los lugares y las metemos en un Holder
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        holder.itemLugarNombre.text = listaLugares[position].nombre
        holder.itemLugarFecha.text = listaLugares[position].fecha
        holder.itemLugarTipo.text = listaLugares[position].tipo
        holder.itemLugarVotos.text = listaLugares[position].votos.toString()
        holder.itemLugarImagen.setImageBitmap(imagenLugar(listaLugares[position]))

        // Queda procesar el botón de favoritos...
        holder.itemLugarFavorito.setOnClickListener {
            Log.i("Lugares", "Has pinchado el favorito de: " + listaLugares[position].id)
        }

        // Programamos el clic de cada fila (itemView)
        holder.itemLugarImagen
            .setOnClickListener {
                // Devolvemos la noticia
                accionPrincipal(listaLugares[position])
            }
    }

    /**
     * Elimina un item de la lista
     *
     * @param position
     */
    fun removeItem(position: Int) {
        listaLugares.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listaLugares.size)
    }

    /**
     * Recupera un Item de la lista
     *
     * @param item
     * @param position
     */
    fun restoreItem(item: Lugar, position: Int) {
        listaLugares.add(position, item)
        notifyItemInserted(position)
        notifyItemRangeChanged(position, listaLugares.size)
    }

    /**
     * Para añadir un elemento
     * @param item
     */
    fun addItem(item: Lugar) {
        listaLugares.add(item)
        notifyDataSetChanged()
    }


    /**
     * Devuelve el número de items de la lista
     *
     * @return
     */
    override fun getItemCount(): Int {
        return listaLugares.size
    }

    /**
     * Devuelve la imagen de un lugar
     * @param lugar Lugar
     * @return Bitmap?
     */
    private fun imagenLugar(lugar: Lugar): Bitmap? {
        try {
            val fotografia = FotografiaController.selectById(lugar.imagenID)
            return ImageBase64.toBitmap(fotografia?.imagen.toString())
        } catch (ex: Exception) {
            return null
        }
    }

    /**
     * Holder que encapsula los objetos a mostrar en la lista
     */
    class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Elementos graficos con los que nos asociamos
        var itemLugarImagen = itemView.itemLugarImagen
        var itemLugarNombre = itemView.itemLugarNombre
        var itemLugarFecha = itemView.itemLugarFecha
        var itemLugarTipo = itemView.itemLugarTipo
        var itemLugarVotos = itemView.itemLugarVotos
        var itemLugarFavorito = itemView.itemLugarFavorito

    }
}
