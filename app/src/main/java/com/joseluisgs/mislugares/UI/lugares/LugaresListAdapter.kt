package com.joseluisgs.mislugares.UI.lugares

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaController
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarController
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

        // procesamos el ffavorito
        // color
        colorBotonFavorito(position, holder)
        // Queda procesar el botón de favoritos...
        holder.itemLugarFavorito.setOnClickListener {
            eventoBotonFavorito(position, holder)

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
    fun updateItem(item: Lugar, position: Int) {
        listaLugares[position] = item
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
     * Procesa el favorito
     * @param position Int
     */
    private fun eventoBotonFavorito(position: Int, holder: LugarViewHolder) {
        // Cambiamos el favorito
        listaLugares[position].favorito = !listaLugares[position].favorito
        // Procesamos el color
        colorBotonFavorito(position, holder)
        // Procesamos el número de votos
        if(listaLugares[position].favorito)
            listaLugares[position].votos ++
        else
            listaLugares[position].votos --

        LugarController.update(listaLugares[position])
        holder.itemLugarVotos.text = listaLugares[position].votos.toString()
        Log.i("Favorito", listaLugares[position].favorito.toString())
        Log.i("Favorito", listaLugares[position].votos.toString())
    }

    /**
     * Pone el color del fondo del botom de favoritos
     * @param position Int
     * @param holder LugarViewHolder
     */
    private fun colorBotonFavorito(
        position: Int,
        holder: LugarViewHolder
    ) {
        if (listaLugares[position].favorito)
            holder.itemLugarFavorito.backgroundTintList =
                AppCompatResources.getColorStateList(holder.context, R.color.favOnColor)
        else
            holder.itemLugarFavorito.backgroundTintList =
                AppCompatResources.getColorStateList(holder.context, R.color.favOffColor)
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
        var context = itemView.context

    }
}
