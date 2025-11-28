package com.example.monastery360

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.monastery360.manager.FavoritesManager
import com.example.monastery360.model.Monastery
import com.example.monastery360.repository.MonasteryRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : BaseActivity() {

    // --- UI & Navigation Variables ---
    private lateinit var recyclerRecommended: RecyclerView
    private lateinit var showAllRecommended: TextView

    private lateinit var navHome: LinearLayout
    private lateinit var navFav: LinearLayout
    private lateinit var navPlan: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navHomeIcon: ImageView
    private lateinit var navFavIcon: ImageView
    private lateinit var navPlanIcon: ImageView
    private lateinit var navProfileIcon: ImageView
    private lateinit var navHomeText: TextView
    private lateinit var navFavText: TextView
    private lateinit var navPlanText: TextView
    private lateinit var navProfileText: TextView
    private val selectedTintColor by lazy { Color.parseColor("#5B4ECC") }
    private val defaultTintColor by lazy { Color.parseColor("#808080") }
    private var currentSelectedIndex = 0

    // --- Adapter & Location Variables ---
    private lateinit var monasteryAdapter: MonasteryAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorefragment)

        // Initialize FavoritesManager
        FavoritesManager.init(this)

        // Initialize all UI views first
        initNavViews()

        // Location client ko initialize karein
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup all sections
        setupPopularMonasteries()
        setupRecommendedSection()
        setupBottomNavigation()
        setupVirtualToursClick()
        setupWeatherUpdateClick() // ✅ NEW: Weather Update setup

        // Set initial selected state for nav
        setSelectedNav(0)

        // Request location to update distances
        requestLocationAndCalculateDistances()
    }

    override fun onResume() {
        super.onResume()
        // Jab favorites se wapas aaye, home highlight ho
        setSelectedNav(0)
    }

    // --- SECTION SETUP FUNCTIONS ---

    // --- "POPULAR" SECTION SETUP (Kotlin-Only Change) ---
    private fun setupPopularMonasteries() {
        // 1. Jin monasteries ko dikhana hai, unke naam.
        val popularNames = listOf(
            "Rumtek Monastery",
            "Sangachoeling Monastery",
            "Tashiding Monastery",
            "Enchey Monastery",
            "Dubdi Monastery"
        )

        // 2. Repository se data nikalein.
        val allMonasteries = MonasteryRepository.getAllMonasteries()

        // 3. Parent layout ko dhoondhein.
        val popularScrollView = findViewById<HorizontalScrollView>(R.id.popularScrollView)
        // HorizontalScrollView ke andar ek hi child hai (LinearLayout)
        val popularContainer = popularScrollView.getChildAt(0) as? LinearLayout

        // Agar container mil gaya, to aage badhein.
        popularContainer?.let { container ->
            // 4. Har ek popular naam ke liye loop chalayein.
            popularNames.forEachIndexed { index, monasteryName ->

                // 5. Container ke andar child (vertical LinearLayout) ko uski position se pakdein.
                if (index < container.childCount) {
                    val popularItemView = container.getChildAt(index) as? LinearLayout

                    popularItemView?.let { itemView ->
                        // 6. Us child ke andar ImageView aur TextView ko unki position se pakdein.
                        val cardView = itemView.getChildAt(0) as? androidx.cardview.widget.CardView
                        val imageView = cardView?.getChildAt(0) as? ImageView
                        val textView = itemView.getChildAt(1) as? TextView

                        // 7. Repository se monastery ka data dhoondhein.
                        val monastery = allMonasteries.find { it.name == monasteryName }

                        // 8. Data set karein (Image aur Text).
                        if (imageView != null && textView != null) {
                            textView.text = monasteryName // Text set kiya.

                            // Image set karein.
                            if (monastery != null && monastery.images.isNotEmpty()) {
                                Glide.with(this)
                                    .load(monastery.images[0]) // Dynamic Cloudinary Link
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_placeholder) // Optional
                                    .into(imageView)
                            } else {
                                imageView.setImageResource(R.drawable.ic_placeholder)
                            }

                            // Click listener lagayein.
                            itemView.setOnClickListener {
                                val intent = Intent(this, MonasteryDetailActivity::class.java)
                                intent.putExtra("MONASTERY_NAME", monasteryName)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun setupPopularItem(imageViewId: Int, textViewId: Int, monastery: Monastery) {
        val imageView = findViewById<ImageView>(imageViewId)
        val textView = findViewById<TextView>(textViewId)

        textView.text = monastery.name

        if (monastery.images.isNotEmpty()) {
            Glide.with(this)
                .load(monastery.images[0])
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder)
        }

        imageView.setOnClickListener {
            val intent = Intent(this, MonasteryDetailActivity::class.java)
            intent.putExtra("MONASTERY_NAME", monastery.name)
            startActivity(intent)
        }
    }

    private fun setupRecommendedSection() {
        recyclerRecommended = findViewById(R.id.recyclerRecommended)
        showAllRecommended = findViewById(R.id.showAllRecommended)

        val recommendedList = MonasteryRepository.getAllMonasteries().take(6)

        monasteryAdapter = MonasteryAdapter(
            list = recommendedList,
            isVertical = false,
            onItemClick = { monastery ->
                val intent = Intent(this, MonasteryDetailActivity::class.java)
                intent.putExtra("MONASTERY_NAME", monastery.name)
                startActivity(intent)
            }
        )

        recyclerRecommended.adapter = monasteryAdapter
        recyclerRecommended.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Snapping and spacing for RecyclerView
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerRecommended)
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        recyclerRecommended.addItemDecoration(CardPeekItemDecoration(spacing))

        // "Show All" button listener
        showAllRecommended.setOnClickListener {
            val intent = Intent(this, AllMonasteriesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    // --- VIRTUAL TOURS SETUP ---
    private fun setupVirtualToursClick() {
        try {
            val gridWhatWeOffer = findViewById<android.widget.GridLayout>(R.id.gridWhatWeOffer)
            val virtualToursCard = gridWhatWeOffer.getChildAt(0) as? androidx.cardview.widget.CardView

            virtualToursCard?.setOnClickListener {
                showMonasterySelectionDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ✅ NEW: WEATHER UPDATE SETUP
    private fun setupWeatherUpdateClick() {
        try {
            val gridWhatWeOffer = findViewById<android.widget.GridLayout>(R.id.gridWhatWeOffer)

            // Weather Update card ko find karein (usually 2nd card - index 1)
            // Agar tumhare grid me card ki position different hai, to index change kar sakte ho
            val weatherUpdateCard = gridWhatWeOffer.getChildAt(1) as? androidx.cardview.widget.CardView

            weatherUpdateCard?.setOnClickListener {
                // MonasteryWeatherActivity ko open karein
                val intent = Intent(this, MonasteryWeatherActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Weather feature temporarily unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMonasterySelectionDialog() {
        val allMonasteries = MonasteryRepository.getAllMonasteries()
        val monasteryNames = allMonasteries.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Monastery for Aerial View")
            .setItems(monasteryNames) { dialog, which ->
                val selectedMonastery = allMonasteries[which]
                openVirtualTours(selectedMonastery.latitude, selectedMonastery.longitude, selectedMonastery.name)
            }
            .show()
    }

    private fun openVirtualTours(lat: Double, lng: Double, monasteryName: String) {
        val earthUri = Uri.parse("https://earth.google.com/web/search/$monasteryName/@$lat,$lng,500a,35y,0h,0t,0r")
        val earthIntent = Intent(Intent.ACTION_VIEW, earthUri)
        earthIntent.setPackage("com.google.android.apps.earth")

        try {
            startActivity(earthIntent)
        } catch (e: Exception) {
            openGoogleMapsSatelliteView(lat, lng)
        }
    }

    private fun openGoogleMapsSatelliteView(lat: Double, lng: Double) {
        val mapUri = Uri.parse("geo:$lat,$lng?z=18")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            val webUri = Uri.parse("https://maps.google.com/?q=$lat,$lng&z=18")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    // --- LOCATION FUNCTIONS ---

    private fun requestLocationAndCalculateDistances() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    monasteryAdapter.updateDistances(location)
                } else {
                    Toast.makeText(this, "Could not get location. Ensure GPS is on.", Toast.LENGTH_SHORT).show()
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
            } else {
                Toast.makeText(this, "Permission denied. Distance cannot be calculated.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- NAVIGATION AND UI HELPERS ---

    private fun initNavViews() {
        navHome = findViewById(R.id.navHome)
        navFav = findViewById(R.id.navFav)
        navPlan = findViewById(R.id.navPlan)
        navProfile = findViewById(R.id.navProfile)
        navHomeIcon = findViewById(R.id.navHomeIcon)
        navFavIcon = findViewById(R.id.navFavIcon)
        navPlanIcon = findViewById(R.id.navPlanIcon)
        navProfileIcon = findViewById(R.id.navProfileIcon)
        navHomeText = findViewById(R.id.navHomeText)
        navFavText = findViewById(R.id.navFavText)
        navPlanText = findViewById(R.id.navPlanText)
        navProfileText = findViewById(R.id.navProfileText)
    }

    private fun setupBottomNavigation() {
        navHome.setOnClickListener { setSelectedNav(0) }
        navFav.setOnClickListener {
            setSelectedNav(1)
            // Navigate to FavoritesActivity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        navPlan.setOnClickListener { setSelectedNav(2) }
        navProfile.setOnClickListener {
            setSelectedNav(3)

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    private fun setSelectedNav(selectedIndex: Int) {
        if (currentSelectedIndex == selectedIndex && selectedIndex != 0) return // Allow re-selecting home
        currentSelectedIndex = selectedIndex

        val navItems = listOf(navHome, navFav, navPlan, navProfile)
        val navIcons = listOf(navHomeIcon, navFavIcon, navPlanIcon, navProfileIcon)
        val navTexts = listOf(navHomeText, navFavText, navPlanText, navProfileText)

        navItems.forEachIndexed { index, itemLayout ->
            val icon = navIcons[index]
            val text = navTexts[index]

            if (index == selectedIndex) {
                itemLayout.orientation = LinearLayout.HORIZONTAL
                text.visibility = View.VISIBLE
                itemLayout.setBackgroundResource(R.drawable.nav_item_active_background)
                icon.setColorFilter(selectedTintColor, PorterDuff.Mode.SRC_IN)
                text.setTextColor(selectedTintColor)
            } else {
                itemLayout.orientation = LinearLayout.VERTICAL
                text.visibility = View.GONE
                itemLayout.setBackgroundColor(Color.TRANSPARENT)
                icon.setColorFilter(defaultTintColor, PorterDuff.Mode.SRC_IN)
                text.setTextColor(defaultTintColor)
            }
        }
    }

    inner class CardPeekItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position < state.itemCount - 1) {
                outRect.right = spacing
            }
        }
    }
}