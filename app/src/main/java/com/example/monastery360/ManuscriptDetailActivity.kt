package com.example.monastery360

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.monastery360.model.DigitalArchive

// Class ab BaseActivity se inherit kar rahi hai
class ManuscriptDetailActivity : BaseActivity() {

    // Views (Sirf zaroori views rakhe gaye hain)
    private lateinit var btnBack: ImageView
    private lateinit var imgManuscript: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtDesc: TextView

    private var manuscript: DigitalArchive? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuscript_detail)
        android.util.Log.d("ManuscriptDetail", "Activity created")

        initViews()
        loadManuscriptData()
        setupClickListeners()
    }

    private fun initViews() {
        try {
            // Sirf maujooda views ko initialize karein
            btnBack = findViewById(R.id.btnBack)
            imgManuscript = findViewById(R.id.imgManuscript)
            txtTitle = findViewById(R.id.txtTitle)
            txtDesc = findViewById(R.id.txtDesc)
            // scrollView aur baaki button-related views ko hata diya gaya hai

            android.util.Log.d("ManuscriptDetail", "All required views initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("ManuscriptDetail", "Error initializing views: ${e.message}", e)
            Toast.makeText(this, "Error loading UI: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadManuscriptData() {
        // Data nikalne ka safe tareeka
        manuscript = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("MANUSCRIPT", DigitalArchive::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("MANUSCRIPT")
        }

        if (manuscript == null) {
            android.util.Log.e("ManuscriptDetail", "No manuscript data received")
            Toast.makeText(this, "No manuscript data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val currentManuscript = manuscript!!
        android.util.Log.d("ManuscriptDetail", "Loading manuscript: ${currentManuscript.title}")

        // UI par data set karein
        txtTitle.text = currentManuscript.title
        txtDesc.text = currentManuscript.description

        // Image load karein
        if (currentManuscript.images.isNotEmpty()) {
            val imageUrl = currentManuscript.images.first()
            android.util.Log.d("ManuscriptDetail", "Loading image: $imageUrl")
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .into(imgManuscript)
        } else {
            android.util.Log.w("ManuscriptDetail", "No images available for manuscript")
            imgManuscript.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun setupClickListeners() {
        // Sirf back button ka listener bacha hai
        btnBack.setOnClickListener {
            android.util.Log.d("ManuscriptDetail", "Back button clicked")
            finish()
        }
        // Baaki sabhi button listeners hata diye gaye hain
    }

    // toggleExpand(), expand(), collapse(), openImagePicker(), aur startTranscription() functions
    // poori tarah se hata diye gaye hain.

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("ManuscriptDetail", "Activity destroyed")
    }
}
