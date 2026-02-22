package com.example.app1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etProfesion = findViewById<EditText>(R.id.etProfesion)
        val etFrase = findViewById<EditText>(R.id.etFrase)
        val btnGenerar = findViewById<Button>(R.id.btnGenerar)

        btnGenerar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val profesion = etProfesion.text.toString()
            val frase = etFrase.text.toString()

            val intent = Intent(this, TarjetaActivity::class.java)
            intent.putExtra("NOMBRE", nombre)
            intent.putExtra("PROFESION", profesion)
            intent.putExtra("FRASE", frase)
            startActivity(intent)
        }
    }
}