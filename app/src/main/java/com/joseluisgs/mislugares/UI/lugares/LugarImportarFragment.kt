package com.joseluisgs.mislugares.UI.lugares

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.joseluisgs.mislugares.Entidades.Lugares.Lugar
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.CaptureActivity
import kotlinx.android.synthetic.main.fragment_importar_lugar.*

class LugarImportarFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_importar_lugar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.visibility = View.INVISIBLE
        scanQRCode()
        // Iniciamos la interfaz
        initUI()
    }

    /**
     * Inicia la UI
     */
    private fun initUI() {

    }

    /**
     * Escanea el código
     */
    private fun scanQRCode() {
        val integrator = IntentIntegrator.forSupportFragment(this).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt(getString(R.string.escaneando_codigo))
        }
        integrator.initiateScan()
    }

    /**
     * Procesamos los resultados
     * @param requestCode Int
     * @param resultCode Int
     * @param data [ERROR : Intent]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_LONG).show()
                volver()
            }
            else {
                try {
                    val lugar = Gson().fromJson(result.contents, Lugar::class.java)
                    Toast.makeText(context, "Recuperado: $lugar", Toast.LENGTH_LONG).show()
                    view?.visibility = View.VISIBLE
                    //abrirDetalle(lugar)
                } catch (ex: Exception) {
                    Toast.makeText(context, "Error: El QR no es de un lugar válido", Toast.LENGTH_LONG).show()
                    volver()
                }
                Toast.makeText(context, "Recuperado: " + result.contents, Toast.LENGTH_LONG).show()

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
    * Vuelve
    */
    private fun volver() {
        val listaLugares = LugaresFragment()
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.add(R.id.nav_host_fragment, listaLugares)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun abrirDetalle(lugar: Lugar) {

    }



}