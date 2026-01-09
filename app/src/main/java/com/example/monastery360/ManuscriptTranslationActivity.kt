package com.example.monastery360

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream

// ===== DATA CLASSES =====
data class VisionRequest(
    @SerializedName("requests") val requests: List<VisionRequestItem>
)

data class VisionRequestItem(
    @SerializedName("image") val image: ImageContent,
    @SerializedName("features") val features: List<Feature>
)

data class ImageContent(
    @SerializedName("content") val content: String
)

data class Feature(
    @SerializedName("type") val type: String
)

data class VisionResponse(
    @SerializedName("responses") val responses: List<VisionResponseItem>
)

data class VisionResponseItem(
    @SerializedName("fullTextAnnotation") val fullTextAnnotation: FullTextAnnotation?,
    @SerializedName("error") val error: ErrorDetail?
)

data class FullTextAnnotation(
    @SerializedName("text") val text: String?,
    @SerializedName("pages") val pages: List<Page>?
)

data class Page(
    @SerializedName("property") val property: Property?
)

data class Property(
    @SerializedName("detectedLanguages") val detectedLanguages: List<Language>?
)

data class Language(
    @SerializedName("languageCode") val languageCode: String?
)

data class ErrorDetail(
    @SerializedName("message") val message: String?
)

data class TranslationResponse(
    @SerializedName("data") val data: TranslationData?,
    @SerializedName("error") val error: ErrorDetail?
)

data class TranslationData(
    @SerializedName("translations") val translations: List<TranslationItem>
)

data class TranslationItem(
    @SerializedName("translatedText") val translatedText: String
)

// ===== RETROFIT INTERFACE =====
interface GoogleVisionService {
    @POST("v1/images:annotate")
    fun detectText(
        @Body request: VisionRequest,
        @Query("key") apiKey: String
    ): Call<VisionResponse>
}

interface GoogleTranslateService {
    @FormUrlEncoded
    @POST("v2")
    fun translate(
        @Field("q") text: String,
        @Field("target") target: String,
        @Field("key") apiKey: String
    ): Call<TranslationResponse>
}

// ===== MAIN ACTIVITY =====
class ManuscriptTranslationActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION = 101

    // Views
    private lateinit var imgPreview: ImageView
    private lateinit var txtDetectedText: TextView
    private lateinit var txtTranslatedText: TextView
    private lateinit var spinnerLanguage: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var cardResults: CardView
    private lateinit var btnBack: ImageView
    private lateinit var btnRetranslate: android.widget.Button

    // API Keys - Retrieve from AndroidManifest.xml metadata
    private val VISION_API_KEY: String by lazy {
        val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        ai.metaData.getString("com.google.android.vision.API_KEY") ?: ""
    }
    private val TRANSLATE_API_KEY: String by lazy {
        val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        ai.metaData.getString("com.google.android.translate.API_KEY") ?: ""
    }

    private var selectedBitmap: Bitmap? = null
    private var currentPhotoFile: java.io.File? = null
    private var lastDetectedText: String = ""  // Store detected text for re-translation

    // Activity Result Launchers
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && currentPhotoFile != null) {
            try {
                selectedBitmap = BitmapFactory.decodeFile(currentPhotoFile!!.absolutePath)
                if (selectedBitmap != null) {
                    imgPreview.setImageBitmap(selectedBitmap)
                    processImage()
                } else {
                    Toast.makeText(this, "Failed to load bitmap", Toast.LENGTH_SHORT).show()
                    Log.e("ManuscriptTranslation", "Bitmap is null after decode")
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error loading photo: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ManuscriptTranslation", "Camera error: ${e.stackTraceToString()}")
            }
        } else {
            Toast.makeText(this, "Camera cancelled or file null", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imgPreview.setImageBitmap(selectedBitmap)
            processImage()
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuscript_translation)

        initViews()
        setupLanguageSpinner()
        setupClickListeners()
        checkCameraPermission()
    }

    private fun initViews() {
        imgPreview = findViewById(R.id.imgPreview)
        txtDetectedText = findViewById(R.id.txtDetectedText)
        txtTranslatedText = findViewById(R.id.txtTranslatedText)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        progressBar = findViewById(R.id.progressBar)
        cardResults = findViewById(R.id.cardResults)
        btnBack = findViewById(R.id.btnBack)
        btnRetranslate = findViewById(R.id.btnCopyTranslated)  // Reuse button for retranslate
    }

    private fun setupLanguageSpinner() {
        val languages = listOf(
            "English - en",
            "Hindi - hi",
            "Sanskrit - sa",
            "Spanish - es",
            "French - fr",
            "German - de",
            "Italian - it",
            "Portuguese - pt",
            "Russian - ru",
            "Japanese - ja",
            "Korean - ko",
            "Chinese - zh",
            "Arabic - ar",
            "Bengali - bn",
            "Latin - la",
            "Greek - el"
        )
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages
        )
        spinnerLanguage.adapter = adapter

        // Set default to Hindi (index 1)
        spinnerLanguage.setSelection(1)

        // Add listener for language change
        spinnerLanguage.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                Log.d("ManuscriptTranslation", "Language changed to: ${languages[position]}")
                // Agar text detect ho chuka hai to re-translate karo
                if (lastDetectedText.isNotEmpty()) {
                    Log.d("ManuscriptTranslation", "Re-translating detected text...")
                    translateText(lastDetectedText)
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        // Check intent action from ManuscriptListActivity
        when (intent.action) {
            "upload_manuscript" -> {
                galleryLauncher.launch("image/*")
            }
            "transcribe_manuscript" -> {
                openCamera()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        try {
            currentPhotoFile = createImageFile()
            val photoUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                currentPhotoFile!!
            )
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("ManuscriptTranslation", "Camera error: ${e.stackTraceToString()}")
        }
    }

    private fun createImageFile(): java.io.File {
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return java.io.File.createTempFile("IMG_", ".jpg", storageDir)
    }

    private fun processImage() {
        if (selectedBitmap == null) return

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                cardResults.visibility = View.GONE

                val base64Image = bitmapToBase64(selectedBitmap!!)

                // Step 1: Detect text using Vision API
                detectTextInImage(base64Image)

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ManuscriptTranslationActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("ManuscriptTranslation", "Exception: ${e.stackTraceToString()}")
            }
        }
    }

    private fun detectTextInImage(base64Image: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://vision.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GoogleVisionService::class.java)

        val request = VisionRequest(
            requests = listOf(
                VisionRequestItem(
                    image = ImageContent(content = base64Image),
                    features = listOf(Feature(type = "DOCUMENT_TEXT_DETECTION"))
                )
            )
        )

        Log.d("ManuscriptTranslation", "Sending Vision API request...")
        Log.d("ManuscriptTranslation", "API Key: ${VISION_API_KEY.take(20)}...")

        service.detectText(request, VISION_API_KEY).enqueue(object : Callback<VisionResponse> {
            override fun onResponse(call: Call<VisionResponse>, response: Response<VisionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val visionData = response.body()!!

                    if (visionData.responses.isEmpty()) {
                        showError("No response from Vision API")
                        return
                    }

                    val firstResponse = visionData.responses[0]

                    if (firstResponse.error != null) {
                        showError("Vision API Error: ${firstResponse.error.message}")
                        return
                    }

                    val detectedText = firstResponse.fullTextAnnotation?.text
                    val detectedLanguage = firstResponse.fullTextAnnotation?.pages?.getOrNull(0)
                        ?.property?.detectedLanguages?.getOrNull(0)?.languageCode ?: "en"

                    if (detectedText.isNullOrEmpty()) {
                        showError("No text detected in image")
                        return
                    }

                    Log.d("ManuscriptTranslation", "Detected Text: $detectedText")
                    Log.d("ManuscriptTranslation", "Detected Language: $detectedLanguage")

                    lastDetectedText = detectedText  // Store for re-translation
                    txtDetectedText.text = detectedText

                    // Step 2: Translate text
                    translateText(detectedText)

                } else {
                    showError("Vision API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<VisionResponse>, t: Throwable) {
                showError("Network Error: ${t.message}")
                Log.e("ManuscriptTranslation", "Vision API Failure: ${t.message}")
            }
        })
    }

    private fun translateText(text: String) {
        val selectedLanguage = spinnerLanguage.selectedItem.toString()
        val targetLanguage = selectedLanguage.split(" - ")[1]

        val retrofit = Retrofit.Builder()
            .baseUrl("https://translation.googleapis.com/language/translate/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GoogleTranslateService::class.java)

        Log.d("ManuscriptTranslation", "Translating to: $targetLanguage")
        Log.d("ManuscriptTranslation", "Text to translate: $text")
        Log.d("ManuscriptTranslation", "Translate API Key: ${TRANSLATE_API_KEY.take(20)}...")

        service.translate(text, targetLanguage, TRANSLATE_API_KEY).enqueue(object : Callback<TranslationResponse> {
            override fun onResponse(call: Call<TranslationResponse>, response: Response<TranslationResponse>) {
                Log.d("ManuscriptTranslation", "Translation API Response: Code=${response.code()}, Success=${response.isSuccessful}")
                progressBar.visibility = View.GONE

                try {
                    if (response.isSuccessful && response.body() != null) {
                        val translationData = response.body()!!
                        Log.d("ManuscriptTranslation", "Translation Data: $translationData")

                        if (translationData.error != null) {
                            showError("Translation Error: ${translationData.error.message}")
                            Log.e("ManuscriptTranslation", "Translation error: ${translationData.error.message}")
                            return
                        }

                        val translatedText = translationData.data?.translations?.getOrNull(0)?.translatedText

                        if (translatedText.isNullOrEmpty()) {
                            showError("No translation received")
                            Log.e("ManuscriptTranslation", "Translation received but empty")
                            return
                        }

                        Log.d("ManuscriptTranslation", "Translated: $translatedText")
                        Log.d("ManuscriptTranslation", "Setting text to UI...")

                        txtDetectedText.text = text
                        txtTranslatedText.text = translatedText
                        cardResults.visibility = View.VISIBLE

                        Log.d("ManuscriptTranslation", "UI Updated Successfully")

                    } else {
                        val errorBody = response.errorBody()?.string() ?: "No error body"
                        showError("Translation API Error: ${response.code()}")
                        Log.e("ManuscriptTranslation", "Translation API Error: Code=${response.code()}, Error=$errorBody")
                    }
                } catch (e: Exception) {
                    Log.e("ManuscriptTranslation", "Exception in onResponse: ${e.stackTraceToString()}")
                    showError("Error: ${e.message}")
                }
            }

            override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("Network Error: ${t.message}")
                Log.e("ManuscriptTranslation", "Translation Failure: ${t.stackTraceToString()}")
            }
        })
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        cardResults.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }
}