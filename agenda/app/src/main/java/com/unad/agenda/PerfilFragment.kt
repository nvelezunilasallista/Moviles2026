package com.unad.agenda

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File

// Objeto singleton: guarda el perfil en memoria mientras la app esté abierta
object PerfilData {
    var nombre: String = ""
    var correo: String = ""
    var telefono: String = ""
    var fotoUri: Uri? = null
    var latitud: Double = 0.0
    var longitud: Double = 0.0
}

class PerfilFragment : Fragment() {

    private lateinit var ivFoto: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etLatitud: EditText
    private lateinit var etLongitud: EditText

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

    // Launcher para abrir la CÁMARA
    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito) {
            ivFoto.setImageURI(uriFotoCamara)
            PerfilData.fotoUri = uriFotoCamara
        }
    }

    // Launcher para pedir permiso de ubicación al usuario
    private val permisosLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        if (concedido) {
            obtenerUbicacion()
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
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
        etLatitud  = view.findViewById(R.id.etLatitud)
        etLongitud = view.findViewById(R.id.etLongitud)

        cargarDatos()

        ivFoto.setOnClickListener { mostrarOpcionesFoto() }

        view.findViewById<Button>(R.id.btnObtenerUbicacion).setOnClickListener {
            pedirUbicacion()
        }

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            guardarDatos()
        }

        view.findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas salir?")
                .setPositiveButton("Sí") { _, _ ->
                    SessionManager(requireContext()).cerrarSesion()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun cargarDatos() {
        // Intentar cargar desde la base de datos al abrir la app
        val db = PerfilDatabase(requireContext())
        val hayDatosGuardados = db.cargar(PerfilData)

        if (hayDatosGuardados) {
            // llenar los campos con lo que se encuentra en la BBDD
            etNombre.setText(PerfilData.nombre)
            etCorreo.setText(PerfilData.correo)
            etTelefono.setText(PerfilData.telefono)
            etLatitud.setText(PerfilData.latitud.toString())
            etLongitud.setText(PerfilData.longitud.toString())
            if (PerfilData.fotoUri != null) {
                ivFoto.setImageURI(PerfilData.fotoUri)
            }
        }
    }

    private fun guardarDatos() {
        // Actualizar el objeto en memoria
        PerfilData.nombre   = etNombre.text.toString().trim()
        PerfilData.correo   = etCorreo.text.toString().trim()
        PerfilData.telefono = etTelefono.text.toString().trim()

        // Persistir en SQLite para que se conserve al cerrar la app
        PerfilDatabase(requireContext()).guardar(
            PerfilData.nombre,
            PerfilData.correo,
            PerfilData.telefono,
            PerfilData.fotoUri?.toString(),
            PerfilData.latitud,
            PerfilData.longitud
        )

        Toast.makeText(requireContext(), "Perfil guardado", Toast.LENGTH_SHORT).show()
    }

    // Paso 1: verificar si ya tenemos permiso, si no pedirlo
    private fun pedirUbicacion() {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(requireContext(), permiso) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            permisosLauncher.launch(permiso)
        }
    }

    // Paso 2: ya tenemos permiso, obtener la ubicación actual
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        val cliente = LocationServices.getFusedLocationProviderClient(requireActivity())

        cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion ->
                if (ubicacion != null) {
                    // GPS encendido y con señal: mostrar las coordenadas
                    etLatitud.setText(ubicacion.latitude.toString())
                    etLongitud.setText(ubicacion.longitude.toString())
                    PerfilData.latitud  = ubicacion.latitude
                    PerfilData.longitud = ubicacion.longitude
                } else {
                    // GPS apagado o sin señal: mostrar cero
                    Toast.makeText(requireContext(), "GPS apagado o sin señal", Toast.LENGTH_SHORT).show()
                    etLatitud.setText("0.0")
                    etLongitud.setText("0.0")
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
                etLatitud.setText("0.0")
                etLongitud.setText("0.0")
            }
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
        val archivo = File.createTempFile("foto_perfil", ".jpg", requireContext().cacheDir)
        uriFotoCamara = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            archivo
        )
        camaraLauncher.launch(uriFotoCamara)
    }
}
