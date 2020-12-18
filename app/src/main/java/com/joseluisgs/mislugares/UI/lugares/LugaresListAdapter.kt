package com.joseluisgs.mislugares.UI.lugares

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaDTO
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaMapper
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarDTO
import com.joseluisgs.mislugares.Entidades.Lugares.LugarMapper
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import kotlinx.android.synthetic.main.item_lugar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LugaresListAdapter(
    private val listaLugares: MutableList<Lugar>,
    // Famos a tener distintas acciones y eventos
    private val accionPrincipal: (Lugar) -> Unit,

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
        imagenLugar(listaLugares[position], holder)

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
     */
    private fun imagenLugar(lugar: Lugar, holder: LugarViewHolder) {
        val clientREST = MisLugaresAPI.service
        val call: Call<FotografiaDTO> = clientREST.fotografiaGetById(lugar.imagenID)
        call.enqueue((object : Callback<FotografiaDTO> {

            override fun onResponse(call: Call<FotografiaDTO>, response: Response<FotografiaDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "fotografiasGetById ok")
                    var remoteFotografia = FotografiaMapper.fromDTO(response.body() as FotografiaDTO)
                    holder.itemLugarImagen.setImageBitmap(ImageBase64.toBitmap(remoteFotografia.imagen))
                } else {
                    Log.i("REST", "Error: fotografiasGetById isSuccessful")
                    holder.itemLugarImagen.setImageBitmap(BitmapFactory.decodeResource(holder.context?.resources,
                        R.drawable.ic_mapa))
                }
            }

            override fun onFailure(call: Call<FotografiaDTO>, t: Throwable) {
                Toast.makeText(holder.context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))
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
        if (listaLugares[position].favorito)
            listaLugares[position].votos++
        else
            listaLugares[position].votos--

        // Actualizamos los lugares
        actualizarLugarVotos(listaLugares[position], holder)
    }

    /**
     * Actualiza los votos de un lugar
     * @param lugar Lugar
     * @param holder LugarViewHolder
     */
    private fun actualizarLugarVotos(lugar: Lugar, holder: LugarViewHolder) {
        val clientREST = MisLugaresAPI.service
        val lugarDTO = LugarMapper.toDTO(lugar)

        val call: Call<LugarDTO> = clientREST.lugarUpdate(lugar.id, lugarDTO)
        call.enqueue((object : Callback<LugarDTO> {

            override fun onResponse(call: Call<LugarDTO>, response: Response<LugarDTO>) {
                if (response.isSuccessful) {
                    Log.i("REST", "lugarUpdate ok")

                    holder.itemLugarVotos.text = lugar.votos.toString()
                    Log.i("Favorito", lugar.favorito.toString())
                    Log.i("Favorito", lugar.votos.toString())
                } else {
                    Log.i("REST", "Error: lugarUpdate isSuccessful")
                }
            }

            override fun onFailure(call: Call<LugarDTO>, t: Throwable) {
                Toast.makeText(holder.context,
                    "Error al acceder al servicio: " + t.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            }
        }))

    }

    /**
     * Pone el color del fondo del botom de favoritos
     * @param position Int
     * @param holder LugarViewHolder
     */
    private fun colorBotonFavorito(
        position: Int,
        holder: LugarViewHolder,
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
