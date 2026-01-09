package com.example.monastery360

import android.Manifest
import android.content.Context
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
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.monastery360.manager.FavoritesManager
import com.example.monastery360.model.Monastery
import com.example.monastery360.repository.MonasteryRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

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
        setupWeatherUpdateClick()
        setupPlanTripClick()
        setupTravelTipsClick()

        // Set initial selected state for nav
        setSelectedNav(0)

        // Request location to update distances
        requestLocationAndCalculateDistances()
    }

    private fun setupPlanTripClick() {
        try {
            val gridWhatWeOffer = findViewById<GridLayout>(R.id.gridWhatWeOffer)

            // Plan Trip card (usually index 2 - 3rd card)
            val planTripCard = gridWhatWeOffer.getChildAt(2) as? CardView

            planTripCard?.setOnClickListener {
                val intent = Intent(this, PlanningActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupTravelTipsClick() {
        try {
            val gridWhatWeOffer = findViewById<GridLayout>(R.id.gridWhatWeOffer)

            // Travel Tips card (usually index 3 or 4)
            var travelTipsCard: CardView? = null

            // Try to find by checking card count
            if (gridWhatWeOffer.childCount > 3) {
                travelTipsCard = gridWhatWeOffer.getChildAt(3) as? CardView
            }

            // If not found, iterate through all cards
            if (travelTipsCard == null) {
                for (i in 0 until gridWhatWeOffer.childCount) {
                    val card = gridWhatWeOffer.getChildAt(i) as? CardView
                    if (card != null) {
                        // Check if this is the Travel Tips card by looking for text
                        val textView = card.findViewById<TextView>(android.R.id.text1)
                        if (textView?.text?.toString()?.contains("Travel", ignoreCase = true) == true ||
                            textView?.text?.toString()?.contains("Tips", ignoreCase = true) == true) {
                            travelTipsCard = card
                            break
                        }
                    }
                }
            }

            travelTipsCard?.setOnClickListener {
                // ✅ User ID get kar Firebase se
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                if (userId.isNotEmpty()) {
                    val intent = Intent(this, FeedActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Travel Tips feature unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Jab favorites se wapas aaye, home highlight ho
        setSelectedNav(0)

        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val savedLanguage = prefs.getString("language", "en")
        val currentLanguage = resources.configuration.locale.language

        if (currentLanguage != savedLanguage) {
            recreate()
        }
    }

    // --- SECTION SETUP FUNCTIONS ---

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
                            // ✅ Translated name show karo
                            val translatedName = getTranslatedMonasteryName(monasteryName)
                            textView.text = translatedName

                            // Image set karein.
                            if (monastery != null && monastery.images.isNotEmpty()) {
                                Glide.with(this)
                                    .load(monastery.images[0])
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_placeholder)
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
            val gridWhatWeOffer = findViewById<GridLayout>(R.id.gridWhatWeOffer)
            val virtualToursCard = gridWhatWeOffer.getChildAt(0) as? CardView

            virtualToursCard?.setOnClickListener {
                // Step 1: Monastery list
                val monasteries = arrayOf("Rumtek", "Tashiding Monastery", "Pemayangtse Monastery")

                AlertDialog.Builder(this)
                    .setTitle("Choose Monastery")
                    .setItems(monasteries) { _, which ->
                        val selectedMonastery = monasteries[which]

                        when (selectedMonastery) {
                            "Rumtek" -> {
                                // Step 2: Inner / Outer view options
                                val rumtekViews = arrayOf("inner view", "outer view")
                                val urls = arrayOf(
                                    "https://www.google.com/maps/embed?pb=!4v1765215630473!6m8!1m7!1sCAoSF0NJSE0wb2dLRUlDQWdJQ2tzTVh5b0FF!2m2!1d27.2886859898702!2d88.56146202338051!3f57.41088000000002!4f30!5f0.7820865974627469", // Outer
                                    "https://www.google.com/maps/embed?pb=!4v1765214169929!6m8!1m7!1sCAoSF0NJSE0wb2dLRUlDQWdJRHNqdWI4aWdF!2m2!1d27.30591310250971!2d88.53630179386377!3f275.80063!4f7.42765!5f0.7820865974627469" // Inner
                                )

                                AlertDialog.Builder(this)
                                    .setTitle("Choose View for Rumtek")
                                    .setItems(rumtekViews) { _, index ->
                                        openStreetView(urls[index])
                                    }.show()
                            }

                            "Tashiding Monastery" -> {
                                openStreetView("https://www.google.com/maps/embed?pb=!4vSOME_OTHER_EMBED_URL")
                            }

                            "Pemayangtse Monastery" -> {
                                openStreetView("https://www.google.com/maps/embed?pb=!4vSOME_OTHER_EMBED_URL_2")
                            }
                        }
                    }
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to open StreetViewActivity with iframe
    private fun openStreetView(url: String) {
        val intent = Intent(this, StreetViewActivity::class.java)
        intent.putExtra("iframe_url", url)
        startActivity(intent)
    }

    // ✅ WEATHER UPDATE SETUP
    private fun setupWeatherUpdateClick() {
        try {
            val gridWhatWeOffer = findViewById<android.widget.GridLayout>(R.id.gridWhatWeOffer)
            val weatherUpdateCard = gridWhatWeOffer.getChildAt(1) as? androidx.cardview.widget.CardView

            weatherUpdateCard?.setOnClickListener {
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
        val monasteryNames = allMonasteries.map {
            getTranslatedMonasteryName(it.name)
        }.toTypedArray()

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
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        navPlan.setOnClickListener {
            setSelectedNav(2)
            val intent = Intent(this, PlanningActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        navProfile.setOnClickListener {
            setSelectedNav(3)
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        val navCamera = findViewById<LinearLayout>(R.id.navCamera)
        navCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun setSelectedNav(selectedIndex: Int) {
        if (currentSelectedIndex == selectedIndex && selectedIndex != 0) return
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

    // ===== TRANSLATED MONASTERY NAMES =====
    private fun getTranslatedMonasteryName(monasteryName: String): String {
        return when (monasteryName) {
            "Dubdi Monastery" -> getString(R.string.dubdi_name)
            "Enchey Monastery" -> getString(R.string.enchey_name)
            "Gonjang Monastery" -> getString(R.string.gonjang_name)
            "Kewzing Monastery" -> getString(R.string.kewzing_name)
            "Labrang Monastery" -> getString(R.string.labrang_name)
            "Phodong Monastery" -> getString(R.string.phodong_name)
            "Rinchenpong Monastery" -> getString(R.string.rinchenpong_name)
            "Rumtek Monastery" -> getString(R.string.rumtek_name)
            "Sangachoeling Monastery" -> getString(R.string.sangachoeling_name)
            "Tashiding Monastery" -> getString(R.string.tashiding_name)
            else -> monasteryName
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