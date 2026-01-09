package com.example.monastery360

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocationMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_CODE = 101

    private var monasteryLat = 0.0
    private var monasteryLng = 0.0
    private var monasteryName = ""
    private var currentLocation: Location? = null
    private var currentLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_map)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // ✅ Receive data from previous screen
        monasteryLat = intent.getDoubleExtra("LATITUDE", 27.5768) // Default to Kanyakumari
        monasteryLng = intent.getDoubleExtra("LONGITUDE", 88.5892) // Default to Kanyakumari
        monasteryName = intent.getStringExtra("MONASTERY_NAME") ?: "Monastery"

        // Debug: Check if data received
        android.util.Log.d("LocationMapActivity", "Lat: $monasteryLat, Lng: $monasteryLng, Name: $monasteryName")

        checkPermissionAndLoadMap()
    }

    private fun checkPermissionAndLoadMap() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                LOCATION_PERMISSION_CODE
            )
        } else {
            loadMap()
        }
    }

    private fun loadMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // =============== SAME MAP TYPE AS MonasteryMapsActivity ===============
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        val monasteryLatLng = LatLng(monasteryLat, monasteryLng)

        // =============== MONASTERY MARKER (Orange) ===============
        googleMap.addMarker(
            MarkerOptions()
                .position(monasteryLatLng)
                .title(monasteryName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )

        // =============== SAME 3D CAMERA POSITION AS MonasteryMapsActivity ===============
        val cameraPosition = CameraPosition.Builder()
            .target(monasteryLatLng)
            .zoom(16f)
            .bearing(45f)
            .tilt(60f)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        // =============== UI SETTINGS ===============
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
            isMapToolbarEnabled = false
        }

        // =============== USER LOCATION (Azure blue marker only) ===============
        getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val userLatLng = LatLng(location.latitude, location.longitude)

                currentLocationMarker?.remove()

                currentLocationMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )

                // =============== AUTO-FIT BOTH MARKERS IN VIEW ===============
                val monasteryLatLng = LatLng(monasteryLat, monasteryLng)
                val bounds = LatLngBounds.Builder()
                    .include(userLatLng)
                    .include(monasteryLatLng)
                    .build()

                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))

            } else {
                Toast.makeText(this, "Turn on GPS", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            loadMap()
        }
    }
}