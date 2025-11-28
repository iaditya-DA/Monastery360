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
    private lateinit var showAllRecommended: TextView

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

    private val selectedTintColor by lazy { Color.parseColor("#5B4ECC") }
    private val defaultTintColor by lazy { Color.parseColor("#808080") }
    private var currentSelectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorefragment)

        recyclerRecommended = findViewById(R.id.recyclerRecommended)
        showAllRecommended = findViewById(R.id.showAllRecommended)

        // ✅ Use Repository for data
        val monasteryList = MonasteryRepository.getAllMonasteries()

        // ✅ Pass onItemClick lambda to adapter
        val adapter = MonasteryAdapter(
            list = monasteryList,
            isVertical = false,
            onItemClick = { monastery ->
                // Open MonasteryDetailActivity
                val intent = Intent(this, MonasteryDetailActivity::class.java)
                intent.putExtra("MONASTERY_NAME", monastery.name)
                startActivity(intent)
            }
        )

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

    private fun setupShowAllButton() {
        showAllRecommended.setOnClickListener {
            val intent = Intent(this, AllMonasteriesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }


    inner class CardPeekItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)

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
