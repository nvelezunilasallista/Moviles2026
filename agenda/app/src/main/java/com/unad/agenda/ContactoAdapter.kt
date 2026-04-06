package com.unad.agenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactoAdapter(private val contactos: List<Contacto>) :
    RecyclerView.Adapter<ContactoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvApellidos: TextView = itemView.findViewById(R.id.tvApellidos)
        val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreo)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contacto = contactos[position]
        holder.tvNombre.text = contacto.nombre
        holder.tvApellidos.text = contacto.apellidos
        holder.tvCorreo.text = contacto.correo
        holder.tvTelefono.text = contacto.telefono
    }

    override fun getItemCount(): Int = contactos.size
}
