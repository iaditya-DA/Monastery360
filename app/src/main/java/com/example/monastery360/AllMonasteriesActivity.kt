package com.example.monastery360

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager // <-- Naya import
import android.location.Location // <-- Naya import
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat // <-- Naya import
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.model.Monastery
import com.example.monastery360.repository.MonasteryRepository
import com.google.android.gms.location.FusedLocationProviderClient // <-- Naya import
import com.google.android.gms.location.LocationServices // <-- Naya import

class AllMonasteriesActivity : BaseActivity() {

    // --- Naye variables location ke liye ---
    private lateinit var monasteryAdapter: MonasteryAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 102 // Use a different code than MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_monasteries)

        // Location client ko initialize karein
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAllMonasteries)

        // Adapter ko class-level variable mein store karein
        monasteryAdapter = MonasteryAdapter(
            list = MonasteryRepository.getAllMonasteries(),
            isVertical = true,
            onItemClick = { monastery ->
                openMonasteryDetail(monastery)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllMonasteriesActivity)
            this.adapter = monasteryAdapter // Yahan adapter set hua
            setHasFixedSize(true)
        }

        // --- Location fetch karne ka process shuru karein ---
        requestLocationAndCalculateDistances()
    }

    // --- YEH DO NAYE FUNCTIONS LOCATION KE LIYE HAIN ---

    private fun requestLocationAndCalculateDistances() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // Location mil gayi! Adapter ko update karein.
                        monasteryAdapter.updateDistances(location)
                    } else {
                        Toast.makeText(this, "Could not get location. Please ensure GPS is on.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Agar permission nahi hai, toh user se permission maangein
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Agar user ne permission de di, toh dobara location fetch karein
                requestLocationAndCalculateDistances()
            } else {
                Toast.makeText(this, "Permission denied. Distance cannot be calculated.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- NEECHE KA CODE AAPKA PURANA AUR UNCHANGED HAI ---

    private fun openMonasteryDetail(monastery: Monastery) {
        val intent = Intent(this, MonasteryDetailActivity::class.java)
        intent.putExtra("MONASTERY_NAME", monastery.name)
        startActivity(intent)
    }
}
