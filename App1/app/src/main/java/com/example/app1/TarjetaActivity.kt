package com.example.app1

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TarjetaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta)

        val nombre = intent.getStringExtra("NOMBRE") ?: "Sin nombre"
        val profesion = intent.getStringExtra("PROFESION") ?: "Sin profesión"
        val frase = intent.getStringExtra("FRASE") ?: ""

        findViewById<TextView>(R.id.tvNombre).text = nombre
        findViewById<TextView>(R.id.tvProfesion).text = profesion
        findViewById<TextView>(R.id.tvFrase).text = "\"$frase\""
    }
}