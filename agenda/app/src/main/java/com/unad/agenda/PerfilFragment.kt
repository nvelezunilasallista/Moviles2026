package com.unad.agenda

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import android.widget.Button
import java.io.File

// Objeto singleton: guarda el perfil en memoria mientras la app esté abierta
object PerfilData {
    var nombre: String = ""
    var correo: String = ""
    var telefono: String = ""
    var fotoUri: Uri? = null  // URI de la foto (galería o archivo de cámara)
}

class PerfilFragment : Fragment() {

    private lateinit var ivFoto: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText

    // URI del archivo temporal donde la cámara escribirá la foto
    private var uriFotoCamara: Uri? = null

    // Launcher para abrir la GALERÍA
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            ivFoto.setImageURI(uri)
            PerfilData.fotoUri = uri
        }
    }

    // Launcher para abrir la CÁMARA (foto completa, no thumbnail)
    // Devuelve true si la foto fue tomada con éxito
    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito) {
            // Recargamos la imagen desde el archivo temporal que llenó la cámara
            ivFoto.setImageURI(uriFotoCamara)
            PerfilData.fotoUri = uriFotoCamara
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivFoto     = view.findViewById(R.id.ivFoto)
        etNombre   = view.findViewById(R.id.etNombre)
        etCorreo   = view.findViewById(R.id.etCorreo)
        etTelefono = view.findViewById(R.id.etTelefono)

        cargarDatos()

        ivFoto.setOnClickListener { mostrarOpcionesFoto() }

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            guardarDatos()
        }
    }

    private fun cargarDatos() {
        etNombre.setText(PerfilData.nombre)
        etCorreo.setText(PerfilData.correo)
        etTelefono.setText(PerfilData.telefono)
        if (PerfilData.fotoUri != null) {
            ivFoto.setImageURI(PerfilData.fotoUri)
        }
    }

    private fun guardarDatos() {
        PerfilData.nombre   = etNombre.text.toString().trim()
        PerfilData.correo   = etCorreo.text.toString().trim()
        PerfilData.telefono = etTelefono.text.toString().trim()
        Toast.makeText(requireContext(), "Perfil guardado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Galería", "Cámara")
        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar foto")
            .setItems(opciones) { _, seleccion ->
                if (seleccion == 0) {
                    galeriaLauncher.launch("image/*")
                } else {
                    abrirCamara()
                }
            }
            .show()
    }

    private fun abrirCamara() {
        // Creamos un archivo vacío en la caché de la app para que la cámara escriba ahí la foto
        val archivo = File.createTempFile("foto_perfil", ".jpg", requireContext().cacheDir)

        // FileProvider convierte la ruta del archivo en una URI segura que la cámara puede usar
        uriFotoCamara = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            archivo
        )

        camaraLauncher.launch(uriFotoCamara)
    }
}
