package com.example.pm1e2_grupo5

import Models.Persona
import Models.RestApiMethods
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class EditPersonaActivity : AppCompatActivity() {
    var editTextNombre: EditText? = null
    var editTextTelefono: EditText? = null
    var editTextLatitud: EditText? = null
    var editTextLongitud: EditText? = null
    var buttonUpdate: Button? = null
    private var persona: Persona? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_persona)

        // Obtener referencias a los elementos de la vista
        editTextNombre = findViewById<View>(R.id.editTextNombre) as EditText
        editTextTelefono = findViewById<View>(R.id.editTextTelefono) as EditText
        editTextLatitud = findViewById<View>(R.id.editTextLatitud) as EditText
        editTextLongitud = findViewById<View>(R.id.editTextLongitud) as EditText
        buttonUpdate = findViewById<View>(R.id.buttonUpdate) as Button

        // Obtener los datos de la persona desde el Intent
        val intent = intent
        persona = intent.getSerializableExtra("persona") as Persona?

        // Rellenar los campos con los datos actuales
        editTextNombre!!.setText(persona!!.nombre)
        editTextTelefono!!.setText(persona!!.telefono)
        editTextLatitud!!.setText(persona!!.latitud)
        editTextLongitud!!.setText(persona!!.longitud)

        // Configurar el botón de actualización
        buttonUpdate!!.setOnClickListener { // Obtener los datos modificados
            val nombre = editTextNombre!!.text.toString()
            val telefono = editTextTelefono!!.text.toString()
            val latitud = editTextLatitud!!.text.toString()
            val longitud = editTextLongitud!!.text.toString()

            // Actualizar la persona
            persona!!.nombre = nombre
            persona!!.telefono = telefono
            persona!!.latitud = latitud
            persona!!.longitud = longitud

            // Enviar los datos actualizados al servidor
            updatePersona(persona)
        }
    }

    private fun updatePersona(persona: Persona?) {
        //String url = "http://192.168.100.105/crud-examen/updateperson.php";
        val url = RestApiMethods.EndpointUpdateContacto

        // Crear el objeto JSON con los datos a actualizar
        val jsonObject = JSONObject()
        try {
            jsonObject.put("id", persona!!.id)
            jsonObject.put("nombre", persona.nombre)
            jsonObject.put("telefono", persona.telefono)
            jsonObject.put("latitud", persona.latitud)
            jsonObject.put("longitud", persona.longitud)
            jsonObject.put("firma", persona.firma)
        } catch (e: JSONException) {
            e.printStackTrace()
            return
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.PUT,
            url,
            jsonObject,
            { response ->
                try {
                    val message = response.getString("message")
                    Toast.makeText(this@EditPersonaActivity, message, Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                if (error.networkResponse != null) {
                    val responseBody = String(error.networkResponse.data)
                    Log.e(ContentValues.TAG, "Error al actualizar persona: $responseBody")
                } else {
                    Log.e(ContentValues.TAG, "Error al actualizar persona: $error")
                }
                Toast.makeText(
                    this@EditPersonaActivity,
                    "Error al actualizar persona",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }
}
