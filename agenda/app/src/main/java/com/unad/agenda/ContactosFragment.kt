package com.unad.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactosFragment : Fragment() {

    private val contactos = mutableListOf<Contacto>()
    private lateinit var adapter: ContactoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_contactos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvContactos = view.findViewById<RecyclerView>(R.id.rvContactos)
        adapter = ContactoAdapter(contactos)
        rvContactos.layoutManager = LinearLayoutManager(requireContext())
        rvContactos.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabAgregarContacto).setOnClickListener {
            mostrarDialogAgregarContacto()
        }
    }

    private fun mostrarDialogAgregarContacto() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_agregar_contacto, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar contacto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = dialogView.findViewById<EditText>(R.id.etNombre).text.toString().trim()
                val apellidos = dialogView.findViewById<EditText>(R.id.etApellidos).text.toString().trim()
                val correo = dialogView.findViewById<EditText>(R.id.etCorreo).text.toString().trim()
                val telefono = dialogView.findViewById<EditText>(R.id.etTelefono).text.toString().trim()

                if (nombre.isNotEmpty()) {
                    contactos.add(Contacto(nombre, apellidos, correo, telefono))
                    adapter.notifyItemInserted(contactos.size - 1)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContactosFragment()
    }
}
