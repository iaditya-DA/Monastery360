package com.example.monastery360

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.monastery360.model.Monastery
import com.example.monastery360.repository.MonasteryRepository
import com.example.monastery360.utils.LocaleHelper



class MainActivity : BaseActivity() {

    private lateinit var recyclerRecommended: RecyclerView
    private lateinit var showAllRecommended: TextView  // ✅ Add this

    // Bottom Nav
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

    // ✅ Cache colors and track current selection
    private val selectedTintColor by lazy { Color.parseColor("#5B4ECC") }
    private val defaultTintColor by lazy { Color.parseColor("#808080") }
    private var currentSelectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorefragment)

        recyclerRecommended = findViewById(R.id.recyclerRecommended)
        showAllRecommended = findViewById(R.id.showAllRecommended)  // ✅ Initialize

        // ✅ Use Repository for data
        val monasteryList = MonasteryRepository.getPreviewList()

        val adapter = MonasteryAdapter(monasteryList)
        recyclerRecommended.adapter = adapter
        recyclerRecommended.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // ✅ Add snap helper for smooth card snapping (1 card at a time)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerRecommended)

        // ✅ Add spacing between cards
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        recyclerRecommended.addItemDecoration(CardPeekItemDecoration(spacing))

        // ✅ Setup Show All button
        setupShowAllButton()

        initNavViews()
        setupBottomNavigation()
        setSelectedNav(0)
    }

    // ✅ New function for Show All button
    private fun setupShowAllButton() {
        showAllRecommended.setOnClickListener {
            val intent = Intent(this, AllMonasteriesActivity::class.java)
            startActivity(intent)

            // Optional: Add smooth transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    // ✅ Custom ItemDecoration for spacing between cards
    inner class CardPeekItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)

            // Add spacing between items (except last item)
            if (position < state.itemCount - 1) {
                outRect.right = spacing
            }
        }
    }

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
        navFav.setOnClickListener { setSelectedNav(1) }
        navPlan.setOnClickListener { setSelectedNav(2) }
        navProfile.setOnClickListener { setSelectedNav(3) }
    }

    private fun setSelectedNav(selectedIndex: Int) {
        // ✅ Prevent redundant updates
        if (currentSelectedIndex == selectedIndex) return
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
}