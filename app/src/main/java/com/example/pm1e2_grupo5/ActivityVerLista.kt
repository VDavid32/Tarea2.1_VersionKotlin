package com.example.pm1e2_grupo5

import Models.Persona
import Models.PersonaAdapterGet
import Models.RestApiMethods
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ActivityVerLista : AppCompatActivity() {
    private var listView: ListView? = null
    private var personaAdapter: PersonaAdapterGet? = null
    private var personaList: MutableList<Persona>? = null
    var btneliminar: Button? = null
    var btnactualizar: Button? = null
    var buttonDetails: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_ver_lista)

        //inicio de constructor
        listView = findViewById(R.id.listView)
        personaList = ArrayList()
        personaAdapter = PersonaAdapterGet(this, personaList)
        listView.setAdapter(personaAdapter)
        btneliminar = findViewById<View>(R.id.button2) as Button
        btnactualizar = findViewById<View>(R.id.button3) as Button
        buttonDetails = findViewById<View>(R.id.buttonDetails) as Button

        fetchPersonas()

        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val persona = personaList.get(position)
            val personaId = persona.id
            val lat = persona.latitud
            //Toast.makeText(ActivityVerLista.this, "ID de la persona: " + personaId+" "+lat, Toast.LENGTH_SHORT).show();
        })

        btneliminar!!.setOnClickListener { deleteSelectedPersona() }

        btnactualizar!!.setOnClickListener {
            val position = listView.getCheckedItemPosition()
            if (position != ListView.INVALID_POSITION) {
                val persona = personaList.get(position)
                val intent = Intent(this@ActivityVerLista, EditPersonaActivity::class.java)
                intent.putExtra("persona", persona)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@ActivityVerLista,
                    "Selecciona una persona para editar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonDetails!!.setOnClickListener {
            val position = listView.getCheckedItemPosition()
            if (position != ListView.INVALID_POSITION) {
                val persona = personaList.get(position)
                val intent = Intent(this@ActivityVerLista, DetallesActivity::class.java)
                intent.putExtra("latitud", persona.latitud.toDouble())
                intent.putExtra("longitud", persona.longitud.toDouble())
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@ActivityVerLista,
                    "Selecciona una persona para editar",
                    Toast.LENGTH_SHORT
                ).show()
            }
            /**/
        }

        //modifcar el list cuando ingrese texto
        val editText = findViewById<EditText>(R.id.editTextText)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No se necesita hacer nada aquí
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Muestra el Toast cada vez que el texto cambia
                //Toast.makeText(ActivityVerLista.this, "Texto cambiado: " + s.toString(), Toast.LENGTH_SHORT).show();

                searchPersonas(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                // No se necesita hacer nada aquí
            }
        })

        //fin de modificar list

        //fin contructor
    }


    private fun fetchPersonas() {
        //String url = "http://192.168.100.105/crud-examen/getperson.php"; // Cambia esta URL por la de tu API
        val url = RestApiMethods.EndpointGetContacto
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                for (i in 0 until response.length()) {
                    try {
                        val personaObject = response.getJSONObject(i)
                        val id = personaObject.getString("id").toInt()
                        val nombre = personaObject.getString("nombre")
                        val telefono = personaObject.getString("telefono")
                        val latitud = personaObject.getString("latitud")
                        val longitud = personaObject.getString("longitud")
                        val firma = personaObject.getString("firma") // Obtener la imagen base64

                        /*Log.d(TAG, "nombre: " + nombre);
                                         Log.d(TAG, "telefono: " + telefono);
                                         Log.d(TAG, "latitud: " + latitud);
                                         Log.d(TAG, "longitud: " + longitud);
                                         Log.d(TAG, "firma: " + firma);*/

                        // Crear una instancia de Persona con los datos recibidos
                        val persona = Persona(nombre, telefono, latitud, longitud, firma, id)
                        personaList!!.add(persona) // Agregar persona a la lista
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                // Notificar al adaptador que los datos han cambiado
                personaAdapter!!.notifyDataSetChanged()
            },
            { error -> error.printStackTrace() })

        requestQueue.add(jsonArrayRequest)
    }

    private fun deleteSelectedPersona() {
        val position = listView!!.checkedItemPosition
        if (position != ListView.INVALID_POSITION) {
            val persona = personaList!![position]
            val personaId = persona.id
            //String url = "http://192.168.100.105/crud-examen/deleteperson2.php?id=" + personaId;
            val url = RestApiMethods.EndpointDeleteContacto + personaId

            val stringRequest = StringRequest(
                Request.Method.DELETE,
                url,
                { response ->
                    try {
                        // Intentar convertir la respuesta a JSON
                        val jsonResponse = JSONObject(response)
                        Log.d(TAG, "Persona eliminada: $jsonResponse")
                        personaList!!.removeAt(position)
                        personaAdapter!!.notifyDataSetChanged()
                        Toast.makeText(
                            this@ActivityVerLista,
                            "Persona eliminada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: JSONException) {
                        Log.e(TAG, "Respuesta no es un JSON: $response")
                    }
                },
                { error ->
                    if (error.networkResponse != null) {
                        val responseBody = String(error.networkResponse.data)
                        Log.e(TAG, "Error al eliminar persona: $responseBody")
                    } else {
                        Log.e(TAG, "Error al eliminar persona: $error")
                    }
                    Toast.makeText(
                        this@ActivityVerLista,
                        "Error al eliminar persona",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        } else {
            Toast.makeText(this, "Selecciona una persona para eliminar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchPersonas(query: String) {
        //String url = "http://192.168.100.105/crud-examen/bgetperson.php?query=" + Uri.encode(query);
        val url = RestApiMethods.EndpointBGETContacto + Uri.encode(query)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                personaList!!.clear()
                for (i in 0 until response.length()) {
                    try {
                        val personaObject = response.getJSONObject(i)
                        val id = personaObject.getInt("id")
                        val nombre = personaObject.getString("nombre")
                        val telefono = personaObject.getString("telefono")
                        val latitud = personaObject.getString("latitud")
                        val longitud = personaObject.getString("longitud")
                        val firma = personaObject.getString("firma")

                        val persona = Persona(nombre, telefono, latitud, longitud, firma, id)
                        personaList!!.add(persona)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                personaAdapter!!.notifyDataSetChanged()
            },
            { error ->
                if (error.networkResponse != null) {
                    val responseBody = String(error.networkResponse.data)
                    Log.e(TAG, "Error al buscar persona: $responseBody")
                } else {
                    Log.e(TAG, "Error al buscar persona: $error")
                }
                Toast.makeText(this@ActivityVerLista, "Error al buscar persona", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonArrayRequest)
    }


    companion object {
        private const val TAG = "ActivityVerLista"
    }
}