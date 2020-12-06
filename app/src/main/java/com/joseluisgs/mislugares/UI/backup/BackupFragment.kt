package com.joseluisgs.mislugares.UI.backup

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joseluisgs.mislugares.Entidades.Backup.BackupController
import com.joseluisgs.mislugares.R
import kotlinx.android.synthetic.main.fragment_backup.*

class BackupFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        backupUltimaText.text = BackupController.fechaUltimoBackup(context!!)
        backupArchivarImage.setOnClickListener { exportar() }
        backupImportarImage.setOnClickListener { importar() }
    }

    /**
     * Exportar la información
     */
    private fun exportar() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_exportar)
            .setTitle("Exportar datos")
            .setMessage("¿Desea exportar los datos? Se sobreeescribirá el último fichero de copia de seguridad")
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which -> exportarDatos(context!!) }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    /**
     * importa la información
     */
    private fun importar() {
        AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_desarchivar)
            .setTitle("Importar datos")
            .setMessage("¿Desea importar los datos? Sus datos actuales se sobreeescribirán por los del archivo de copia de seguridad")
            .setPositiveButton(getString(R.string.aceptar)) { dialog, which -> importarDatos() }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    /**
     * Exportar los datos
     */
    private fun exportarDatos(context: Context) {
        // Se archiva y si todo va bien se da un mensaje y cambia la fecha
        val res = BackupController.exportarDatos(context)
        if(res) {
            AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_exportar)
                .setTitle("Exportar datos")
                .setMessage("Copia de Seguridad guardada con éxito")
                .setPositiveButton(getString(R.string.aceptar), null)
                // .setNegativeButton(getString(R.string.cancelar), null)
                .show()
            backupUltimaText.text = BackupController.fechaUltimoBackup(context!!)
        }
        // BackupController.test(context)
    }

    /**
     * Importar los datos
     */
    private fun importarDatos() {
        // Se importar y si todo va bien se da un mensaje
        BackupController.importarDatos()
    }

}