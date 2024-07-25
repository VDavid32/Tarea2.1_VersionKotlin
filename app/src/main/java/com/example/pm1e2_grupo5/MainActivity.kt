package com.example.pm1e2_grupo5

import Models.RestApiMethods
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    var view: View? = null

    var image: Bitmap? = null
    var txtLatitud: EditText? = null
    var txtLongitud: EditText? = null
    var nombre: EditText? = null
    var telefono: EditText? = null
    var tomarfoto: Button? = null
    var guardar: Button? = null
    var contactos: Button? = null

    var actualizacionActiva: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        actualizacionActiva = false
        contactos = findViewById<View>(R.id.btn_contacto) as Button
        txtLatitud = findViewById<View>(R.id.txtLatitud) as EditText
        txtLongitud = findViewById<View>(R.id.txtLongitud) as EditText
        nombre = findViewById<View>(R.id.txtNombre) as EditText
        telefono = findViewById<View>(R.id.txtTelefono) as EditText
        guardar = findViewById<View>(R.id.btn_guardar) as Button
        view = findViewById(R.id.viewfirma) as View

        // geolocalizacion
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            currentLocation
        }

        guardar!!.setOnClickListener {
            if (nombre!!.length() == 0 || telefono!!.length() == 0) {
                Toast.makeText(
                    this@MainActivity,
                    "El campo está vacío o solo contiene espacios.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                enviarDatosAlServidor()
                //ClearScreen();
                finish()
                startActivity(intent)
            }
        }

        contactos!!.setOnClickListener {
            val intent = Intent(applicationContext, ActivityVerLista::class.java)
            startActivity(intent)
        }


        //fin del constrctor
    }

    @get:SuppressLint("MissingPermission")
    private val currentLocation: Unit
        get() {
            fusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location = task.result
                        latitude = location!!.latitude
                        longitude = location.longitude
                        val message = "Lat: $latitude Lon: $longitude"
                        //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        txtLatitud!!.setText("" + latitude)
                        txtLongitud!!.setText("" + longitude)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Unable to get location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                currentLocation
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarDatosAlServidor() {
        //String url = "http://192.168.100.105/crud-examen/postperson2.php";
        val url = RestApiMethods.EndpointPostContacto
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObject = JSONObject()
        try {
            jsonObject.put("nombre", nombre!!.text.toString())
            jsonObject.put("telefono", telefono!!.text.toString())
            jsonObject.put("latitud", txtLatitud!!.text.toString())
            jsonObject.put("longitud", txtLongitud!!.text.toString())
            jsonObject.put("firma", Viewfirma(view)) // Se pasa la cadena base64
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonObject,
            { response ->
                Log.d(ContentValues.TAG, "Response: $response")
                Toast.makeText(
                    this@MainActivity,
                    "Persona guardada exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                finish() // Finaliza la actividad después de guardar los datos
            },
            { error ->
                // Intentar obtener la respuesta en bruto
                if (error.networkResponse != null) {
                    val responseBody = String(error.networkResponse.data)
                    Log.e(ContentValues.TAG, "Error: $responseBody")
                    //Toast.makeText(ActivityAgregarLista.this, "Error al guardar la persona: " + responseBody, Toast.LENGTH_LONG).show();
                } else {
                    Log.e(ContentValues.TAG, "Exito: $error")
                    //Toast.makeText(ActivityAgregarLista.this, "Error al guardar la persona", Toast.LENGTH_SHORT).show();
                }
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

    private fun ClearScreen() {
        nombre!!.setText("")
        telefono!!.setText("")
        view!!.isDrawingCacheEnabled = false
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1


        fun Viewfirma(view5: View?): String {
            view5!!.isDrawingCacheEnabled = true
            val bitmap = view5.drawingCache
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            val byteArray = stream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }
}