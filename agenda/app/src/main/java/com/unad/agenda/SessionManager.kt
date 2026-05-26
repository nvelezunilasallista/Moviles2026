package com.unad.agenda

import android.content.Context
import android.content.SharedPreferences

// Clase que maneja la sesión del usuario usando SharedPreferences
// SharedPreferences es como un "cajón" donde guardamos datos simples entre sesiones
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)

    // Guarda el token cuando el login es exitoso
    fun guardarToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    // Obtiene el token guardado (retorna null si no hay sesión activa)
    fun obtenerToken(): String? {
        return prefs.getString("token", null)
    }

    // Retorna true si el usuario ya inició sesión
    fun estaLogueado(): Boolean {
        return obtenerToken() != null
    }

    // Borra la sesión (útil para un botón de "Cerrar sesión")
    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
