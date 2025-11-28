package com.example.monastery360

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import kotlin.math.roundToInt



// Retrofit Data Classes for Directions API Response
data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline,
    @SerializedName("legs") val legs: List<Leg>
)

data class OverviewPolyline(
    @SerializedName("points") val points: String
)

data class Leg(
    @SerializedName("distance") val distance: Distance,
    @SerializedName("duration") val duration: Duration
)

data class Distance(
    @SerializedName("text") val text: String
)

data class Duration(
    @SerializedName("text") val text: String
)


// Retrofit Interface for API calls
interface DirectionsApiService {
    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Call<DirectionsResponse>
}


class MonasteryMapsActivity : AppCompatActivity(), OnMapReadyCallback, TextToSpeech.OnInitListener {

    // --- All your existing variables ---
    private var googleMap: GoogleMap? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var monasteryName = ""
    private var monasteryAddress = ""

    private lateinit var btnBack: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtLocationDetail: TextView
    private lateinit var txtDistance: TextView
    private lateinit var btnDirections: Button
    private lateinit var btnLanguage: ImageView
    private lateinit var btnMyLocation: ImageView

    private lateinit var btnPlayAudio: ImageView
    private lateinit var txtAudioDuration: TextView
    private lateinit var seekBarAudio: SeekBar
    private lateinit var txtCurrentTime: TextView
    private lateinit var txtTotalTime: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var currentLocationMarker: Marker? = null

    // MODIFIED: Renamed directionPolyline to routePolyline for clarity
    private var routePolyline: Polyline? = null

    private var textToSpeech: TextToSpeech? = null
    private var isTTSInitialized = false
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var currentLanguage = "en"
    private val languages = mapOf("en" to "English", "hi" to "हिंदी", "ne" to "नेपाली")
    private var audioText = ""
    private var estimatedDuration = 0
    private var currentProgress = 0

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "MonasteryMapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monastery_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        latitude = intent.getDoubleExtra("LATITUDE", 27.5768)
        longitude = intent.getDoubleExtra("LONGITUDE", 88.5892)
        monasteryName = intent.getStringExtra("MONASTERY_NAME") ?: "Monastery"
        monasteryAddress = intent.getStringExtra("MONASTERY_ADDRESS") ?: "Address"

        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        currentLanguage = prefs.getString("audio_language", "en") ?: "en"

        textToSpeech = TextToSpeech(this, this)
        updateAudioText()
        initViews()
        setupMapFragment()
        setupAudioGuide()
        checkLocationPermission()
    }

    private fun initViews() {
        // ... (your existing initViews code is unchanged)
        btnBack = findViewById(R.id.btnBackMap)
        txtTitle = findViewById(R.id.txtMapTitle)
        txtLocationDetail = findViewById(R.id.txtLocationDetail)
        txtDistance = findViewById(R.id.txtDistance)
        btnDirections = findViewById(R.id.btnDirections)
        btnLanguage = findViewById(R.id.btnLanguageMap)
        btnMyLocation = findViewById(R.id.btnMyLocationMap)

        txtTitle.text = monasteryName
        txtLocationDetail.text = monasteryAddress

        btnBack.setOnClickListener { finish() }
        btnLanguage.setOnClickListener { showLanguageDialog() }
        btnMyLocation.setOnClickListener { getCurrentLocation(true) }

        // MODIFIED: Directions button now calls getDirections()
        btnDirections.setOnClickListener {
            if (currentLocation != null) {
                // Call the function to draw the route inside the app
                getDirections()
            } else {
                Toast.makeText(this, "Cannot get directions. Your location is not available.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // NEW FUNCTION: To get directions from the API
    // NEW FUNCTION: To get directions from the API
    private fun getDirections() {
        if (currentLocation == null) {
            Toast.makeText(this, "Waiting for current location...", Toast.LENGTH_SHORT).show()
            return
        }

        // Show a message to the user
        Toast.makeText(this, "Fetching directions...", Toast.LENGTH_SHORT).show()

        val origin = "${currentLocation!!.latitude},${currentLocation!!.longitude}"
        val destination = "$latitude,$longitude"

        try {
            // FIXED: Read the API key from the AndroidManifest.xml metadata
            val apiKey = "AIzaSyA4o9p91MHBNWbK-G-Hco_eOUDUBpaIl2Q"


            if (apiKey.isNullOrEmpty()) {
                Toast.makeText(this, "API key not found in Manifest.", Toast.LENGTH_LONG).show()
                return
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(DirectionsApiService::class.java)
            service.getDirections(origin, destination, apiKey).enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val routes = response.body()!!.routes
                        if (routes.isNotEmpty()) {
                            val points = routes[0].overviewPolyline.points
                            drawPolyline(points)

                            // Update distance and duration text from API for more accuracy
                            val leg = routes[0].legs[0]
                            txtDistance.text = "${leg.distance.text} • ${leg.duration.text}"
                            txtDistance.visibility = View.VISIBLE

                        } else {
                            Toast.makeText(this@MonasteryMapsActivity, "No routes found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MonasteryMapsActivity, "Failed to get directions. Check API key and network.", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Directions API Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Toast.makeText(this@MonasteryMapsActivity, "Network error while getting directions.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Directions API Failure: ", t)
                }
            })

        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.message)
            Toast.makeText(this, "Error reading API key from Manifest.", Toast.LENGTH_LONG).show()
        }
    }


    // NEW FUNCTION: To draw the route on the map
    private fun drawPolyline(encoded: String) {

        // Clear old line
        routePolyline?.remove()

        val decodedPath = PolyUtil.decode(encoded)

        routePolyline = googleMap?.addPolyline(
            PolylineOptions()
                .addAll(decodedPath)
                .color(Color.BLUE)     // 🔥 Google Maps Blue Line
                .width(12f)
                .geodesic(true)
        )

        // 🔥 Auto-fit camera to full route
        if (currentLocation != null) {
            val bounds = LatLngBounds.Builder()
                .include(LatLng(currentLocation!!.latitude, currentLocation!!.longitude))
                .include(LatLng(latitude, longitude))
                .build()

            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
        }
    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        val monasteryLatLng = LatLng(latitude, longitude)

        // Monastery marker
        googleMap?.addMarker(
            MarkerOptions()
                .position(monasteryLatLng)
                .title(monasteryName)
                .snippet(monasteryAddress)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )

        // 🔥 IMPORTANT: Map open होते ही monastery location पर zoom
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(monasteryLatLng, 15f))

        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
            isMapToolbarEnabled = false
        }

        checkLocationPermission()
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(moveCamera: Boolean) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val userLatLng = LatLng(location.latitude, location.longitude)

                // Remove old marker
                currentLocationMarker?.remove()

                // Add new marker
                currentLocationMarker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )

                // 🔥 User ne "my location" dabaya tab hi camera move ho
                if (moveCamera)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))

            } else {
                Toast.makeText(this, "Turn on GPS", Toast.LENGTH_LONG).show()
            }
        }
    }


    // --- All your other functions (onInit, setTTSLanguage, updateAudioText, updateDistance, audio guide functions, etc.) remain here unchanged ---
    // I am including them below for completeness.

    private fun updateDistance() {
        if (currentLocation == null) {
            txtDistance.visibility = View.GONE
            return
        }
        val monasteryLocation = Location("monastery").apply {
            this.latitude = this@MonasteryMapsActivity.latitude
            this.longitude = this@MonasteryMapsActivity.longitude
        }
        val distanceInMeters = currentLocation!!.distanceTo(monasteryLocation)
        val distanceText = if (distanceInMeters > 1000) {
            val distanceInKm = distanceInMeters / 1000
            String.format(Locale.US, "%.1f km away", distanceInKm)
        } else {
            "${distanceInMeters.roundToInt()} m away"
        }
        // Don't show this if a route is active, as the route provides better info
        if (routePolyline == null) {
            txtDistance.text = distanceText
            txtDistance.visibility = View.VISIBLE
        }
    }

    // ... all other functions like onInit, setTTSLanguage, setupAudioGuide, playAudio, pauseAudio, formatTime, etc. go here exactly as they were ...

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setTTSLanguage(currentLanguage)
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
            isTTSInitialized = false
        }
    }

    private fun setTTSLanguage(langCode: String) {
        val locale = when (langCode) {
            "hi" -> Locale("hi", "IN")
            "ne" -> Locale("ne", "NP")
            else -> Locale.US
        }
        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
            isTTSInitialized = false
        } else {
            isTTSInitialized = true
            textToSpeech?.setSpeechRate(0.9f)
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    runOnUiThread {
                        isPlaying = true
                        btnPlayAudio.setImageResource(R.drawable.ic_pause_circle)
                        startUpdatingSeekBar()
                    }
                }

                override fun onDone(utteranceId: String?) {
                    runOnUiThread {
                        isPlaying = false
                        btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
                        seekBarAudio.progress = 0
                        txtCurrentTime.text = formatTime(0)
                        currentProgress = 0
                        handler.removeCallbacks(runnable)
                    }
                }

                override fun onError(utteranceId: String?) {
                    runOnUiThread {
                        isPlaying = false
                        btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
                        Toast.makeText(this@MonasteryMapsActivity, "Error playing audio", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun updateAudioText() {
        audioText = when (currentLanguage) {
            "hi" -> buildString {
                append("$monasteryName में आपका स्वागत है। ")
                append("यह $monasteryAddress पर स्थित है। ")
                append("आप अब मानचित्र पर स्थान देख रहे हैं। अपनी आभासी यात्रा का आनंद लें!")
            }
            "ne" -> buildString {
                append("$monasteryName मा स्वागत छ। ")
                append("यो $monasteryAddress मा अवस्थित छ। ")
                append("तपाईं अहिले नक्सामा स्थान हेर्दै हुनुहुन्छ। तपाईंको भर्चुअल यात्राको आनन्द लिनुहोस्!")
            }
            else -> buildString {
                append("Welcome to $monasteryName. ")
                append("Located at $monasteryAddress. ")
                append("You are now viewing the location on the map. Enjoy your virtual tour!")
            }
        }
        val wordCount = audioText.split("\\s+".toRegex()).size
        estimatedDuration = (wordCount * 60) / 150 // Duration in seconds
    }

    private fun setupAudioGuide() {
        btnPlayAudio = findViewById(R.id.btnPlayAudioMap)
        txtAudioDuration = findViewById(R.id.txtAudioDurationMap)
        seekBarAudio = findViewById(R.id.seekBarAudioMap)
        txtCurrentTime = findViewById(R.id.txtCurrentTimeMap)
        txtTotalTime = findViewById(R.id.txtTotalTimeMap)
        updateAudioUI()
        btnPlayAudio.setOnClickListener {
            if (!isTTSInitialized) {
                Toast.makeText(this, "Audio guide is loading...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isPlaying) pauseAudio() else playAudio()
        }
        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentProgress = progress
                    txtCurrentTime.text = formatTime(progress * 1000)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if(isPlaying) {
                    handler.removeCallbacks(runnable)
                }
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(isPlaying) {
                    stopAudio()
                    playAudio()
                }
            }
        })
    }

    private fun updateAudioUI() {
        val languageName = languages[currentLanguage] ?: "English"
        txtAudioDuration.text = "${formatTime(estimatedDuration * 1000)} • $languageName"
        txtTotalTime.text = formatTime(estimatedDuration * 1000)
        seekBarAudio.max = estimatedDuration
        seekBarAudio.progress = 0
        txtCurrentTime.text = formatTime(0)
    }

    private fun showLanguageDialog() {
        val languageOptions = arrayOf("English", "हिंदी (Hindi)", "नेपाली (Nepali)")
        val languageCodes = arrayOf("en", "hi", "ne")
        val currentIndex = languageCodes.indexOf(currentLanguage)
        AlertDialog.Builder(this)
            .setTitle("Select Audio Language")
            .setSingleChoiceItems(languageOptions, currentIndex) { dialog, which ->
                val selectedLang = languageCodes[which]
                if (selectedLang != currentLanguage) {
                    stopAudio()
                    currentLanguage = selectedLang
                    getSharedPreferences("AppSettings", MODE_PRIVATE).edit().putString("audio_language", currentLanguage).apply()
                    updateAudioText()
                    setTTSLanguage(currentLanguage)
                    updateAudioUI()
                    Toast.makeText(this, "Language changed to ${languageOptions[which]}", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupMapFragment() {
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.mapContainer, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getCurrentLocation(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(false)
            } else {
                Toast.makeText(this, "Location permission is required to show your position.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun playAudio() {
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "monasteryAudio")
        textToSpeech?.speak(audioText, TextToSpeech.QUEUE_FLUSH, params, "monasteryAudio")
    }

    private fun pauseAudio() {
        textToSpeech?.stop()
        isPlaying = false
        btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
        handler.removeCallbacks(runnable)
    }

    private fun stopAudio() {
        textToSpeech?.stop()
        isPlaying = false
        btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
        seekBarAudio.progress = 0
        txtCurrentTime.text = formatTime(0)
        currentProgress = 0
        handler.removeCallbacks(runnable)
    }

    private fun startUpdatingSeekBar() {
        runnable = Runnable {
            if (isPlaying && currentProgress < seekBarAudio.max) {
                currentProgress++
                seekBarAudio.progress = currentProgress
                txtCurrentTime.text = formatTime(currentProgress * 1000)
                handler.postDelayed(runnable, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun formatTime(millis: Int): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        stopAudio()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
