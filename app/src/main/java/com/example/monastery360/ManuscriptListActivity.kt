package com.example.monastery360

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.adapter.DigitalArchiveAdapter
import com.example.monastery360.adapters.UploadedManuscriptAdapter
import com.example.monastery360.database.ManuscriptDatabase
import com.example.monastery360.database.ManuscriptRepository
import com.example.monastery360.repository.RumtekManuscriptRepository
import kotlinx.coroutines.launch
import java.io.File

class ManuscriptListActivity : BaseActivity() {

    // Views
    private lateinit var recyclerManuscripts: RecyclerView
    private lateinit var recyclerUploadedManuscripts: RecyclerView
    private lateinit var txtManuscriptCount: TextView
    private lateinit var txtUploadedCount: TextView
    private lateinit var btnBack: ImageView
    private lateinit var emptyStateText: TextView

    // Expandable Buttons
    private var scrollView: NestedScrollView? = null
    private lateinit var btnManuscriptActions: LinearLayout
    private lateinit var layoutExpandableOptions: LinearLayout
    private lateinit var imgExpandIcon: TextView
    private lateinit var btnUploadManuscript: LinearLayout
    private lateinit var btnTranscribeManuscript: LinearLayout
    private var isExpanded = false

    // Database
    private lateinit var repository: ManuscriptRepository

    // Activity Result Launchers
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                Log.d("ManuscriptListActivity", "Image selected from gallery: $uri")
                saveImageLocally(uri)
                collapse()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ManuscriptListActivity", "Error: ${e.stackTraceToString()}")
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuscript_list)

        initViews()
        setupRecyclerViews()
        setupClickListeners()

        // Initialize database
        val db = ManuscriptDatabase.getDatabase(this)
        repository = ManuscriptRepository(db.manuscriptDao())

        // Load uploaded manuscripts
        loadUploadedManuscripts()
    }

    private fun initViews() {
        try {
            // Existing views
            recyclerManuscripts = findViewById(R.id.recyclerManuscripts)
            txtManuscriptCount = findViewById(R.id.txtManuscriptCount)
            btnBack = findViewById(R.id.btnBack)

            // Expandable buttons
            btnManuscriptActions = findViewById(R.id.btnManuscriptActions)
            layoutExpandableOptions = findViewById(R.id.layoutExpandableOptions)
            imgExpandIcon = findViewById(R.id.imgExpandIcon)
            btnUploadManuscript = findViewById(R.id.btnUploadManuscript)
            btnTranscribeManuscript = findViewById(R.id.btnTranscribeManuscript)

            // New views for uploaded manuscripts
            try {
                recyclerUploadedManuscripts = findViewById(R.id.recyclerUploadedManuscripts)
                txtUploadedCount = findViewById(R.id.txtUploadedCount)
                emptyStateText = findViewById(R.id.txtEmptyUploadedState)
            } catch (e: Exception) {
                Log.w("ManuscriptListActivity", "Optional views not found: ${e.message}")
            }

            // ScrollView
            scrollView = findViewById(R.id.scrollView)
            if (scrollView == null) {
                Log.e("ManuscriptListActivity", "NestedScrollView with ID 'scrollView' not found!")
            }

        } catch (e: Exception) {
            Log.e("ManuscriptListActivity", "Error initializing views: ${e.message}", e)
            Toast.makeText(this, "UI Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerViews() {
        // Rumtek Manuscripts (from repository)
        val manuscripts = RumtekManuscriptRepository.getAllRumtekManuscripts()
        val count = manuscripts.size
        txtManuscriptCount.text = "$count Manuscript${if (count > 1) "s" else ""} Available"

        recyclerManuscripts.layoutManager = LinearLayoutManager(this)
        recyclerManuscripts.adapter = DigitalArchiveAdapter(manuscripts) { manuscript ->
            val intent = Intent(this, ManuscriptDetailActivity::class.java)
            intent.putExtra("MANUSCRIPT", manuscript)
            startActivity(intent)
        }

        // Setup uploaded manuscripts RecyclerView
        try {
            recyclerUploadedManuscripts.layoutManager = LinearLayoutManager(this)
        } catch (e: Exception) {
            Log.w("ManuscriptListActivity", "Could not setup uploaded recycler: ${e.message}")
        }
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener { finish() }

        // Expandable buttons
        btnManuscriptActions.setOnClickListener { toggleExpand() }

        // Upload Manuscript - Gallery se select karne ke liye
        btnUploadManuscript.setOnClickListener {
            Log.d("ManuscriptListActivity", "Upload button clicked")
            pickImageLauncher.launch("image/*")
        }

        // Transcribe Manuscript - Camera ke liye
        btnTranscribeManuscript.setOnClickListener {
            Log.d("ManuscriptListActivity", "Transcribe button clicked")
            val intent = Intent(this, ManuscriptTranslationActivity::class.java)
            intent.action = "transcribe_manuscript"
            startActivity(intent)
        }
    }

    // Image ko locally save karne ke liye function
    private fun saveImageLocally(uri: Uri) {
        try {
            Log.d("ManuscriptListActivity", "Starting to save image locally")

            // Gallery se bitmap load karo
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            Log.d("ManuscriptListActivity", "Bitmap loaded successfully")

            // App's internal storage directory banao
            val manuscriptDir = File(getExternalFilesDir(null), "manuscripts")
            if (!manuscriptDir.exists()) {
                manuscriptDir.mkdirs()
                Log.d("ManuscriptListActivity", "Manuscript directory created")
            }

            // Unique filename with timestamp
            val filename = "manuscript_${System.currentTimeMillis()}.jpg"
            val file = File(manuscriptDir, filename)

            // Bitmap ko file mein save karo
            val fos = file.outputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()

            Log.d("ManuscriptListActivity", "Image saved at: ${file.absolutePath}")
            Toast.makeText(this, "Manuscript uploaded successfully!", Toast.LENGTH_SHORT).show()

            // ManuscriptTranslationActivity mein bhejo
            openManuscriptTranslation(file.absolutePath, filename)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("ManuscriptListActivity", "Save error: ${e.stackTraceToString()}")
        }
    }

    // Saved image ko ManuscriptTranslationActivity mein bhejne ke liye
    private fun openManuscriptTranslation(imagePath: String, filename: String) {
        val intent = Intent(this, ManuscriptTranslationActivity::class.java)
        intent.action = "upload_manuscript"
        intent.putExtra("IMAGE_PATH", imagePath)
        intent.putExtra("IMAGE_FILENAME", filename)
        startActivity(intent)
    }

    private fun toggleExpand() {
        isExpanded = !isExpanded
        if (isExpanded) {
            expand()
        } else {
            collapse()
        }
    }

    private fun expand() {
        layoutExpandableOptions.visibility = View.VISIBLE
        imgExpandIcon.animate().rotation(180f).setDuration(300).start()

        scrollView?.post {
            scrollView?.smoothScrollTo(0, btnManuscriptActions.bottom)
        }
    }

    private fun collapse() {
        layoutExpandableOptions.visibility = View.GONE
        imgExpandIcon.animate().rotation(0f).setDuration(300).start()
    }

    // Load uploaded manuscripts from database
    private fun loadUploadedManuscripts() {
        lifecycleScope.launch {
            try {
                val manuscripts = repository.getAllManuscripts()
                Log.d("ManuscriptListActivity", "Loaded ${manuscripts.size} uploaded manuscripts")

                if (manuscripts.isNotEmpty()) {
                    try {
                        txtUploadedCount.text = "${manuscripts.size} Uploaded Manuscript${if (manuscripts.size > 1) "s" else ""}"
                        emptyStateText.visibility = View.GONE
                        recyclerUploadedManuscripts.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Log.w("ManuscriptListActivity", "Could not update UI: ${e.message}")
                    }

                    val adapter = UploadedManuscriptAdapter(
                        manuscripts,
                        onItemClick = { manuscript ->
                            handleManuscriptClick(manuscript)
                        },
                        onDeleteClick = { manuscript ->
                            handleManuscriptDelete(manuscript)
                        }
                    )

                    try {
                        recyclerUploadedManuscripts.adapter = adapter
                    } catch (e: Exception) {
                        Log.w("ManuscriptListActivity", "Could not set adapter: ${e.message}")
                    }
                } else {
                    try {
                        txtUploadedCount.text = "0 Uploaded Manuscripts"
                        emptyStateText.visibility = View.VISIBLE
                        emptyStateText.text = "No uploaded manuscripts yet"
                        recyclerUploadedManuscripts.visibility = View.GONE
                    } catch (e: Exception) {
                        Log.w("ManuscriptListActivity", "Could not show empty state: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ManuscriptListActivity", "Error loading manuscripts: ${e.stackTraceToString()}")
                Toast.makeText(this@ManuscriptListActivity, "Error loading manuscripts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleManuscriptClick(manuscript: com.example.monastery360.database.ManuscriptEntity) {
        Log.d("ManuscriptListActivity", "Clicked: ${manuscript.filename}")
        Toast.makeText(this, "Opened: ${manuscript.filename}", Toast.LENGTH_SHORT).show()

        // Open manuscript details or re-translate
        val intent = Intent(this, ManuscriptTranslationActivity::class.java)
        intent.action = "view_uploaded"
        intent.putExtra("MANUSCRIPT_ID", manuscript.id)
        intent.putExtra("IMAGE_PATH", manuscript.filepath)
        intent.putExtra("DETECTED_TEXT", manuscript.detectedText)
        intent.putExtra("TRANSLATED_TEXT", manuscript.translatedText)
        intent.putExtra("TARGET_LANGUAGE", manuscript.targetLanguage)
        startActivity(intent)
    }

    private fun handleManuscriptDelete(manuscript: com.example.monastery360.database.ManuscriptEntity) {
        lifecycleScope.launch {
            try {
                repository.deleteManuscript(manuscript.id)
                Log.d("ManuscriptListActivity", "Deleted: ${manuscript.filename}")
                Toast.makeText(
                    this@ManuscriptListActivity,
                    "Deleted: ${manuscript.filename}",
                    Toast.LENGTH_SHORT
                ).show()
                loadUploadedManuscripts()  // Refresh list
            } catch (e: Exception) {
                Log.e("ManuscriptListActivity", "Error deleting: ${e.message}")
                Toast.makeText(this@ManuscriptListActivity, "Error deleting manuscript", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when coming back from translation activity
        loadUploadedManuscripts()
    }
}