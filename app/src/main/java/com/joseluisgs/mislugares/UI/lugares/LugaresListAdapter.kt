package com.joseluisgs.mislugares.UI.lugares

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.joseluisgs.mislugares.Entidades.Fotografias.Fotografia
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaDTO
import com.joseluisgs.mislugares.Entidades.Fotografias.FotografiaMapper
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Lugares.LugarDTO
import com.joseluisgs.mislugares.Entidades.Lugares.LugarMapper
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Services.Lugares.MisLugaresAPI
import com.joseluisgs.mislugares.Utilidades.CirculoTransformacion
import com.joseluisgs.mislugares.Utilidades.ImageBase64
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_lugar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LugaresListAdapter(
    private val listaLugares: MutableList<Lugar>,
    // Famos a tener distintas acciones y eventos
    private val accionPrincipal: (Lugar) -> Unit,

    ) : RecyclerView.Adapter<LugaresListAdapter.LugarViewHolder>() {
    // Firebase
    private var FireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "Adapter"
    }

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

        // procesamos el favorito
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
        // Buscamos la fotografia
        val docRef = FireStore.collection("imagenes").document(lugar.imagenID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val miImagen = document.toObject(Fotografia::class.java)
                    Log.i(TAG, "fotografiasGetById ok: ${document.data}")
                    Picasso.get()
                        // .load(R.drawable.user_avatar)
                        .load(miImagen?.uri)
                        .into(holder.itemLugarImagen)
                } else {
                    Log.i(TAG, "Error: No exite fotografía")
                    imagenPorDefecto(holder)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "ERROR: " + exception.localizedMessage)
                imagenPorDefecto(holder)
            }
    }

    /**
     * Inserta una imagen por defecto
     * @param holder LugarViewHolder
     */
    private fun imagenPorDefecto(holder: LugarViewHolder) {
        holder.itemLugarImagen.setImageBitmap(BitmapFactory.decodeResource(holder.context?.resources,
            R.drawable.ic_mapa))
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
        // Obtenemos el lugar y actualiamos solo los campos, no entero
        val lugarRef = FireStore.collection("lugares").document(lugar.id)
        lugarRef
            .update(mapOf(
                "votos" to lugar.votos,
                "favorito" to lugar.favorito
            ))
            .addOnSuccessListener {
                Log.i(TAG, "lugarUpdate ok")
                holder.itemLugarVotos.text = lugar.votos.toString()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error actualiza votos", e) }
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
