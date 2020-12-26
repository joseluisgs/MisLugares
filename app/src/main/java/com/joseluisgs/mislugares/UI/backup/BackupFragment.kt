package com.joseluisgs.mislugares.UI.backup

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.joseluisgs.mislugares.App.MyApp
import com.joseluisgs.mislugares.Entidades.Backup.BackupController
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_backup.*

class BackupFragment : Fragment() {
    // Firebase


    private var RES = false
    private lateinit var USUARIO: Usuario

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    /**
     * Inicia la IU
     */
    private fun initUI() {
        backupProgressBar.visibility = View.GONE
        backupUltimaText.text = BackupController.fechaUltimoBackup(context!!)
        backupArchivarImage.setOnClickListener { exportar() }
        backupImportarImage.visibility = View.GONE
        backupImportarText.visibility = View.GONE
    }

    /**
     * Exportar la información
     */
    private fun exportar() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_exportar)
            .setTitle("Exportar datos")
            .setMessage("¿Desea exportar los datos? Se sobreescribirá el último fichero de copia de seguridad")
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which -> exportarDatos(context!!) }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }


    /**
     * Exportar los datos
     */
    private fun exportarDatos(context: Context) {
        // Se archiva y si todo va bien se da un mensaje y cambia la fecha
        val tareaExportar = TareaExportar()
        tareaExportar.execute()
    }

    /**
     * Tarea asíncrona para exportar
     */
    inner class TareaExportar : AsyncTask<Void?, Void?, Void?>() {
        // Pre-Tarea
        override fun onPreExecute() {
            backupProgressBar.visibility = View.VISIBLE
        }

        // Tarea
        override fun doInBackground(vararg args: Void?): Void? {
            try {
                RES = BackupController.exportarDatos(context!!)
            } catch (e: Exception) {
                RES = false
            }
            return null
        }

        //Post-Tarea
        override fun onPostExecute(args: Void?) {
            backupProgressBar.visibility = View.GONE
            if (RES) {
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_exportar)
                    .setTitle("Exportar datos")
                    .setMessage("Copia de Seguridad guardada con éxito")
                    .setPositiveButton(getString(R.string.aceptar), null)
                    // .setNegativeButton(getString(R.string.cancelar), null)
                    .show()
                backupUltimaText.text = BackupController.fechaUltimoBackup(context!!)
            } else {
                AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_exportar)
                    .setTitle("Error Exportar datos")
                    .setMessage("Ha habido un error al exportar los datos")
                    .setPositiveButton(getString(R.string.aceptar), null)
                    // .setNegativeButton(getString(R.string.cancelar), null)
                    .show()
            }
            RES = false
        }
    }
}