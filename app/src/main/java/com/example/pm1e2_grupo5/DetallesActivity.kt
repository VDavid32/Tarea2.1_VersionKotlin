package com.example.pm1e2_grupo5

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DetallesActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var latitud = 0.0
    private var longitud = 0.0
    private var textViewLatitud: TextView? = null
    private var textViewLongitud: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        textViewLatitud = findViewById(R.id.textViewLatitud)
        textViewLongitud = findViewById(R.id.textViewLongitud)

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        textViewLatitud.setText("" + latitud)
        textViewLongitud.setText("" + longitud)

        val mapController = mapView.getController()
        val startPoint = GeoPoint(latitud, longitud)
        mapController.setCenter(startPoint)
        mapController.setZoom(20.0)

        val startMarker = Marker(mapView)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Ubicación"
        mapView.getOverlays().add(startMarker)

        mapView.invalidate()
    }
}