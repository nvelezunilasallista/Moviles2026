package com.unad.agenda

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private val URL_LOGIN = "https://fakestoreapi.com/auth/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val session = SessionManager(this)

        // Si el usuario ya inició sesión antes, ir directo a la app
        if (session.estaLogueado()) {
            irAMainActivity()
            return
        }

        val etUsuario  = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val tvError    = findViewById<TextView>(R.id.tvError)

        btnLogin.setOnClickListener {
            val usuario  = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty()) {
                tvError.text = "Por favor ingresa usuario y contraseña"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            tvError.visibility = View.GONE

            // lifecycleScope.launch maneja el hilo automáticamente:
            // - withContext(Dispatchers.IO) ejecuta la llamada de red en segundo plano
            // - el resto del código corre en el hilo principal sin necesidad de runOnUiThread
            lifecycleScope.launch {
                val token = withContext(Dispatchers.IO) { llamarApiLogin(usuario, password) }

                if (token != null) {
                    session.guardarToken(token)
                    irAMainActivity()
                } else {
                    tvError.text = "Usuario o contraseña incorrectos"
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun llamarApiLogin(usuario: String, password: String): String? {
        return try {
            val conexion = URL(URL_LOGIN).openConnection() as HttpURLConnection
            conexion.requestMethod = "POST"
            conexion.setRequestProperty("Content-Type", "application/json")
            conexion.doOutput = true

            val cuerpo = JSONObject()
            cuerpo.put("username", usuario)
            cuerpo.put("password", password)

            OutputStreamWriter(conexion.outputStream).use { escritor ->
                escritor.write(cuerpo.toString())
                escritor.flush()
            }

            if (conexion.responseCode == HttpURLConnection.HTTP_CREATED) { // 201
                val respuesta = conexion.inputStream.bufferedReader().readText()
                JSONObject(respuesta).getString("token")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun irAMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
