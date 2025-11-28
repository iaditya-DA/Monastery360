package com.example.rideo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var pickupSpinner: AppCompatSpinner
    private lateinit var dropoffSpinner: AppCompatSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize map
        setupMap()

        // Initialize spinners
        setupLocationSpinners()

        // Ride request button logic
        setupRideRequestLogic()
    }

    private fun setupMap() {
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        mapView = findViewById(R.id.map_view_osm)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(19.0)
        mapView.controller.setCenter(GeoPoint(23.251103, 77.524743))
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }

    private fun setupLocationSpinners() {
        pickupSpinner = findViewById(R.id.input_pickup_location)
        dropoffSpinner = findViewById(R.id.input_dropoff_location)

        val locations = arrayOf(
            "LNCT MAIN",
            "LNCT ELCELLENCE",
            "LNCT OLD SCIENCE",
            "LNCT NEW SCIENCE",
            "LNCT AGRICULTURE",
            "LNCT MCA (RAMNATH GUHA BLOCK)",
            "LNCT CME BLOCK",
            "CV RAMAN BLOCK",
            "LNCT PHARMACY BLOCK",
            "SHRI HANUMAN TEMPLE",
            "CENTRAL LIBRARY",
            "LNCT CAFE 9",
            "HIDDEN CAFE",
            "SUTO CAFE",
            "LNCT BASKETBALL GROUND",
            "LNCT FOOTBALL GROUND",
            "REFFTO DRONE LAB",
            "LNCT CENTRAL WORKSHOP",
            "LNCT IDEA LAB",
            "LNCT BUS STAND",
            "LNCT ARYABHATT AUDITORIUM",
            "RATANPUR BOYS HOSTEL",
            "RAIPUR BOYS HOSTEL",
            "KALYANI GIRLS HOSTEL",
            "LNCT DANCE, MUISC AND GYM CLUB"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        pickupSpinner.adapter = adapter
        dropoffSpinner.adapter = adapter
    }

    private fun setupRideRequestLogic() {
        val btnRequestRide = findViewById<Button>(R.id.btn_request_ride)
        val requestView = findViewById<LinearLayout>(R.id.ride_request_view)
        val statusView = findViewById<ConstraintLayout>(R.id.ride_status_view)

        val lnctLocations = mapOf(
            "LNCT MAIN" to GeoPoint(23.251392511234116, 77.5247384787225),
            "LNCT ELCELLENCE" to GeoPoint(23.25005453556064, 77.52228009071233),
            "LNCT OLD SCIENCE" to GeoPoint(23.25030873127484, 77.52601284679683),
            "LNCT NEW SCIENCE" to GeoPoint(23.2497224646325, 77.52777443057941),
            "LNCT AGRICULTURE" to GeoPoint(23.249808786262093, 77.5287256858025),
            "LNCT MCA (RAMNATH GUHA BLOCK)" to GeoPoint(23.249845599663054, 77.52836010443491),
            "LNCT CME BLOCK" to GeoPoint(23.248891364583024, 77.52499289160696),
            "CV RAMAN BLOCK" to GeoPoint(23.25049935729951, 77.52495981112644),
            "LNCT PHARMACY BLOCK" to GeoPoint(23.249087439542055, 77.5276501958869),
            "SHRI HANUMAN TEMPLE" to GeoPoint(23.251487326420854, 77.52370906237275),
            "CENTRAL LIBRARY" to GeoPoint(23.25005843201248, 77.52566156337996),
            "LNCT CAFE 9" to GeoPoint(23.250304537241863, 77.52669405741315),
            "HIDDEN CAFE" to GeoPoint(23.250597835169412, 77.52654508687048),
            "SUTO CAFE" to GeoPoint(23.25048920638257, 77.52641503322211),
            "LNCT BASKETBALL GROUND" to GeoPoint(23.249548218416667, 77.52231532180012),
            "LNCT FOOTBALL GROUND" to GeoPoint(23.249846045486503, 77.52352755490435),
            "REFFTO DRONE LAB" to GeoPoint(23.2493065803438, 77.52275232564051),
            "LNCT CENTRAL WORKSHOP" to GeoPoint(23.250017206294984, 77.52526479115247),
            "LNCT IDEA LAB" to GeoPoint(23.250202012408238, 77.52562219522491),
            "LNCT BUS STAND" to GeoPoint(23.251277994630925, 77.52315104615973),
            "LNCT ARYABHATT AUDITORIUM" to GeoPoint(23.250865446863394, 77.52325109842376),
            "RATANPUR BOYS HOSTEL" to GeoPoint(23.250455139927954, 77.52354393431897),
            "RAIPUR BOYS HOSTEL" to GeoPoint(23.250475318986606, 77.52314860586107),
            "KALYANI GIRLS HOSTEL" to GeoPoint(23.250450655692262, 77.52398074786194),
            "LNCT DANCE, MUISC AND GYM CLUB" to GeoPoint(23.250480972582967, 77.52694507531348)
        )

        btnRequestRide.setOnClickListener {
            val fromName = pickupSpinner.selectedItem.toString()
            val toName = dropoffSpinner.selectedItem.toString()

            val fromPoint = lnctLocations[fromName]
            val toPoint = lnctLocations[toName]

            if (fromPoint == null || toPoint == null) return@setOnClickListener

            requestView.visibility = View.GONE
            statusView.visibility = View.VISIBLE

            // Update status card
            val statusFrom = findViewById<TextView>(R.id.status_from)
            val statusTo = findViewById<TextView>(R.id.status_to)
            statusFrom.text = "From: $fromName"
            statusTo.text = "To: $toName"

            mapView.overlays.clear()

            // Pickup marker
            val pickupMarker = Marker(mapView)
            pickupMarker.position = fromPoint
            pickupMarker.title = "Pickup: $fromName"
            pickupMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(pickupMarker)

            // Drop-off marker
            val dropMarker = Marker(mapView)
            dropMarker.position = toPoint
            dropMarker.title = "Destination: $toName"
            dropMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(dropMarker)

            // Draw road in background thread
            Thread {
                val roadManager: RoadManager = OSRMRoadManager(this, "RIDEO_APP")
                val road: Road = roadManager.getRoad(arrayListOf(fromPoint, toPoint))

                runOnUiThread {
                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                    roadOverlay.outlinePaint.color = 0xFF1E88E5.toInt()  // Blue
                    roadOverlay.outlinePaint.strokeWidth = 10f
                    mapView.overlays.add(roadOverlay)

                    mapView.controller.animateTo(fromPoint)
                    mapView.invalidate()
                }
            }.start()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
