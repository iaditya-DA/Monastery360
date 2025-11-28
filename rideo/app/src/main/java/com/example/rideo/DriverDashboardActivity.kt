package com.example.rideo

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class DriverDashboardActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var switchOnline: Switch
    private lateinit var textStatus: TextView
    private lateinit var statusMainText: TextView
    private lateinit var statusSubText: TextView
    private lateinit var statusIcon: ImageView
    private lateinit var statusCard: MaterialCardView
    private lateinit var toggleLayout: LinearLayout
    private lateinit var menuButton: ImageView
    private lateinit var rideActionButtons: LinearLayout
    private lateinit var btnAcceptRide: Button
    private lateinit var btnRejectRide: Button

    private var mediaPlayer: MediaPlayer? = null  // 🎵 for ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activtity_driver_dashboard)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<ConstraintLayout>(R.id.driver_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        mapView = findViewById(R.id.driver_map_view)
        switchOnline = findViewById(R.id.switch_online)
        textStatus = findViewById(R.id.text_status)
        statusMainText = findViewById(R.id.status_main_text)
        statusSubText = findViewById(R.id.status_sub_text)
        statusIcon = findViewById(R.id.status_icon)
        statusCard = findViewById(R.id.driver_status_card)
        toggleLayout = findViewById(R.id.driver_online_toggle)
        menuButton = findViewById(R.id.driver_btn_menu)
        rideActionButtons = findViewById(R.id.ride_action_buttons)
        btnAcceptRide = findViewById(R.id.btn_accept_ride)
        btnRejectRide = findViewById(R.id.btn_reject_ride)

        setupMap()

        // Online/Offline toggle
        switchOnline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) setOnlineMode() else setOfflineMode()
        }

        // Menu button
        menuButton.setOnClickListener {
            Toast.makeText(this, "Menu clicked!", Toast.LENGTH_SHORT).show()
        }

        // Ride action buttons
        btnAcceptRide.setOnClickListener {
            Toast.makeText(this, "Ride Accepted!", Toast.LENGTH_SHORT).show()
            stopRingtone()
            rideActionButtons.visibility = LinearLayout.GONE
            statusSubText.text = "Ride accepted. Heading to pickup..."
        }

        btnRejectRide.setOnClickListener {
            Toast.makeText(this, "Ride Rejected!", Toast.LENGTH_SHORT).show()
            stopRingtone()
            rideActionButtons.visibility = LinearLayout.GONE
            statusSubText.text = "Waiting ride requests..."
        }

        // Simulate incoming ride request
        mapView.postDelayed({
            showRideRequestNotification("Pickup: LNCT MAIN", "Destination: LNCT CAFE 9")
        }, 5000)
    }

    private fun setupMap() {
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val entryPoint = GeoPoint(23.251103, 77.524743)
        mapView.controller.setZoom(19.0)
        mapView.controller.setCenter(entryPoint)
    }

    private fun setOnlineMode() {
        textStatus.text = "ONLINE"
        statusMainText.text = "YOU'RE ONLINE"
        statusSubText.text = "Waiting ride requests..."
        statusIcon.setImageResource(R.drawable.ic_satellite_dish)
        toggleLayout.setBackgroundResource(R.drawable.rounded_toggle_background)
    }

    private fun setOfflineMode() {
        textStatus.text = "OFFLINE"
        statusMainText.text = "YOU'RE OFFLINE"
        statusSubText.text = "You won't receive ride requests."
        statusIcon.setImageResource(R.drawable.ic_offline)
        toggleLayout.setBackgroundResource(R.drawable.rounded_toggle_background_off)
    }

    private fun showRideRequestNotification(pickup: String, destination: String) {
        statusMainText.text = "RIDE REQUEST"
        statusSubText.text = "$pickup → $destination"
        rideActionButtons.visibility = LinearLayout.VISIBLE
        playRingtone() // 🔔 Start ringtone when request comes
    }

    // 🔔 Play ringtone until accepted or rejected
    private fun playRingtone() {
        stopRingtone() // Ensure no duplicate playback
        mediaPlayer = MediaPlayer.create(this, R.raw.ride_request_tone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    // 📴 Stop ringtone
    private fun stopRingtone() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
    }
}
