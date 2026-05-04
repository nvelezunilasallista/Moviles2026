package com.unad.agenda

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

// SQLiteOpenHelper se encarga de crear y versionar la base de datos automáticamente
class PerfilDatabase(context: Context) : SQLiteOpenHelper(
    context,
    "agenda.db",  // nombre del archivo que Android crea en el almacenamiento interno
    null,
    1             // versión: si cambias la estructura de la tabla, sube este número
) {

    // Android llama a onCreate() la primera vez que se abre la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE perfil (
                id        INTEGER PRIMARY KEY,
                nombre    TEXT,
                correo    TEXT,
                telefono  TEXT,
                foto_uri  TEXT,
                latitud   REAL,
                longitud  REAL
            )
        """.trimIndent())
    }

    // Android llama a onUpgrade() si la versión sube (útil para agregar columnas en el futuro)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS perfil")
        onCreate(db)
    }

    // Guarda el perfil. Siempre usa id=1 porque solo hay un perfil por usuario.
    // CONFLICT_REPLACE: si ya existe una fila con id=1, la reemplaza; si no, la inserta.
    fun guardar(
        nombre: String,
        correo: String,
        telefono: String,
        fotoUri: String?,
        latitud: Double,
        longitud: Double
    ) {
        val valores = ContentValues().apply {
            put("id",       1)
            put("nombre",   nombre)
            put("correo",   correo)
            put("telefono", telefono)
            put("foto_uri", fotoUri)
            put("latitud",  latitud)
            put("longitud", longitud)
        }
        writableDatabase.insertWithOnConflict(
            "perfil", null, valores, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    // Carga el perfil guardado y lo escribe en el objeto PerfilData.
    // Devuelve true si encontró datos, false si la tabla está vacía (primera vez).
    fun cargar(perfil: PerfilData): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM perfil WHERE id = 1", null
        )

        // moveToFirst() devuelve false si no hay filas: no hay nada guardado todavía
        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }

        perfil.nombre   = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))   ?: ""
        perfil.correo   = cursor.getString(cursor.getColumnIndexOrThrow("correo"))   ?: ""
        perfil.telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")) ?: ""
        perfil.latitud  = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"))
        perfil.longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))

        val uriTexto = cursor.getString(cursor.getColumnIndexOrThrow("foto_uri"))
        perfil.fotoUri = if (uriTexto != null) Uri.parse(uriTexto) else null

        cursor.close()
        return true
    }
}
