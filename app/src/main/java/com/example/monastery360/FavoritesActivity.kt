package com.example.monastery360

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.manager.FavoritesManager
import com.example.monastery360.repository.MonasteryRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var backButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var filterIcon: ImageView
    private lateinit var monasteryAdapter: MonasteryAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Initialize FavoritesManager - MOST IMPORTANT!
        FavoritesManager.init(this)

        try {
            recyclerFavorites = findViewById(R.id.recyclerFavorites)
            emptyText = findViewById(R.id.emptyText)
            backButton = findViewById(R.id.backButton)
            searchEditText = findViewById(R.id.searchEditText)
            filterIcon = findViewById(R.id.filterIcon)

            // Location client initialize karo
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            backButton?.setOnClickListener {
                finish()
                // Back ho kar home screen par aao
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            loadFavorites()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFavorites() {
        val favorites = FavoritesManager.getFavorites()
        val allMonasteries = MonasteryRepository.getAllMonasteries()
        val favoriteMonasteries = allMonasteries.filter { it.name in favorites }

        if (favoriteMonasteries.isEmpty()) {
            emptyText.visibility = TextView.VISIBLE
            recyclerFavorites.visibility = RecyclerView.GONE
        } else {
            emptyText.visibility = TextView.GONE
            recyclerFavorites.visibility = RecyclerView.VISIBLE

            monasteryAdapter = MonasteryAdapter(
                list = favoriteMonasteries,
                isVertical = true,
                onItemClick = { monastery ->
                    val intent = Intent(this, MonasteryDetailActivity::class.java)
                    intent.putExtra("MONASTERY_NAME", monastery.name)
                    startActivity(intent)
                }
            )

            recyclerFavorites.adapter = monasteryAdapter
            recyclerFavorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            // Location se distance calculate karo
            requestLocationAndCalculateDistances()
        }
    }

    private fun requestLocationAndCalculateDistances() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    monasteryAdapter.updateDistances(location)
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationAndCalculateDistances()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites() // Reload jab wapas aate ho
    }
}