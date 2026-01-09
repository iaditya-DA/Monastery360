package com.example.monastery360

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

// ===== DATA CLASSES =====
data class ChatGPTRequest(
    @SerializedName("model") val model: String = "gpt-4o",
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 2000 // Increased for detailed response
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: List<ContentPart>
)

data class ContentPart(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null
)

data class ImageUrl(
    @SerializedName("url") val url: String,
    @SerializedName("detail") val detail: String = "high" // Better image analysis
)

data class ChatGPTResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("message") val message: MessageResponse
)

data class MessageResponse(
    @SerializedName("content") val content: String
)

// ===== RETROFIT INTERFACE =====
interface ChatGPTApiService {
    @POST("v1/chat/completions")
    fun analyzeMonasteryImage(
        @Header("Authorization") authHeader: String,
        @Body request: ChatGPTRequest
    ): Call<ChatGPTResponse>
}

// ===== MAIN ACTIVITY =====
class CameraActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 101
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imgCaptured: ImageView
    private lateinit var txtMonasteryDetails: TextView
    private lateinit var btnCapture: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: ImageView

    private var capturedBitmap: Bitmap? = null
    private val CHATGPT_API_KEY = "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" // ⚠️ Replace with actual key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        initViews()
        checkCameraPermission()
    }

    private fun initViews() {
        imgCaptured = findViewById(R.id.imgCapturedMonastery)
        txtMonasteryDetails = findViewById(R.id.txtMonasteryDetails)
        btnCapture = findViewById(R.id.btnCapture)
        progressBar = findViewById(R.id.progressBar)
        btnBack = findViewById(R.id.btnBackCamera)

        btnCapture.setOnClickListener { openCamera() }
        btnBack.setOnClickListener { finish() }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
            android.util.Log.e("CameraActivity", "Camera error: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            capturedBitmap = data?.extras?.get("data") as? Bitmap
            if (capturedBitmap != null) {
                imgCaptured.setImageBitmap(capturedBitmap)
                imgCaptured.visibility = View.VISIBLE
                txtMonasteryDetails.text = "📸 Photo captured! Analyzing..."
                sendImageToChatGPT(capturedBitmap!!)
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendImageToChatGPT(bitmap: Bitmap) {
        progressBar.visibility = View.VISIBLE
        txtMonasteryDetails.text = "🤖 Analyzing monastery image...\nThis may take 15-30 seconds"

        try {
            // Compress image to reduce size and processing time
            val base64Image = bitmapToBase64Optimized(bitmap)

            // Create OkHttpClient with extended timeouts
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
                .retryOnConnectionFailure(true)        // Auto retry on failure
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ChatGPTApiService::class.java)

            // Optimized prompt for better accuracy
            val contentParts = listOf(
                ContentPart(
                    type = "text",
                    text = """
                        Analyze this image and identify if it's a monastery/temple in Sikkim, India.
                        
                        KEY MONASTERIES IN SIKKIM:
                        • Rumtek (Dharma Chakra): Gold roof, red walls, seat of Karmapa
                        • Tashiding: White chorten, hilltop, sacred site
                        • Pemayangtse: 3-story, oldest Nyingma monastery
                        • Enchey: 200+ years old, Gangtok city
                        • Dubdi: Highest, oldest (1701)
                        • Phodong: Twin monastery, 260 monks
                        • Ralang: Sacred mask dance festivals
                        • Sangachoeling: Second oldest (1697)
                        
                        RESPONSE FORMAT (be concise but accurate):
                        
                        🏛️ MONASTERY: [Name or "Unknown"]
                        📍 LOCATION: [District/Town]
                        📅 ESTABLISHED: [Year]
                        
                        🔍 KEY FEATURES:
                        - Architecture style
                        - Visible elements (roof color, walls, prayer flags)
                        - Unique identifiers
                        
                        📜 SIGNIFICANCE:
                        [2-3 lines about importance]
                        
                        🎫 VISITOR INFO:
                        - Best time: [Month/Season]
                        - Entry: [Free/Paid]
                        - Nearby: [1-2 attractions]
                        
                        ⚠️ CONFIDENCE: [High/Medium/Low]
                        
                        If not recognizable, describe what you see and suggest it might be a different monastery.
                    """.trimIndent()
                ),
                ContentPart(
                    type = "image_url",
                    imageUrl = ImageUrl(
                        url = "data:image/jpeg;base64,$base64Image",
                        detail = "high"
                    )
                )
            )

            val request = ChatGPTRequest(
                model = "gpt-4o",
                messages = listOf(Message(role = "user", content = contentParts)),
                maxTokens = 2000
            )

            android.util.Log.d("CameraActivity", "Sending request to ChatGPT...")

            service.analyzeMonasteryImage(
                "Bearer $CHATGPT_API_KEY",
                request
            ).enqueue(object : Callback<ChatGPTResponse> {
                override fun onResponse(
                    call: Call<ChatGPTResponse>,
                    response: Response<ChatGPTResponse>
                ) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val details = response.body()!!.choices.firstOrNull()?.message?.content
                        if (details != null) {
                            txtMonasteryDetails.text = details
                            txtMonasteryDetails.visibility = View.VISIBLE
                            android.util.Log.d("CameraActivity", "Success: Response received")
                        } else {
                            txtMonasteryDetails.text = "❌ No response from API"
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        txtMonasteryDetails.text = """
                            ❌ API Error (${response.code()})
                            
                            Possible causes:
                            • Invalid API key
                            • Rate limit exceeded
                            • Network issue
                            
                            Details: $errorBody
                        """.trimIndent()
                        android.util.Log.e("CameraActivity", "Error ${response.code()}: $errorBody")
                    }
                }

                override fun onFailure(call: Call<ChatGPTResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE

                    val errorMessage = when {
                        t.message?.contains("timeout", ignoreCase = true) == true -> {
                            """
                                ⏱️ Request Timeout
                                
                                The analysis took too long. Try:
                                • Check internet connection
                                • Retake photo (smaller size)
                                • Try again in a moment
                            """.trimIndent()
                        }
                        t.message?.contains("Unable to resolve host", ignoreCase = true) == true -> {
                            """
                                🌐 Network Error
                                
                                Cannot reach OpenAI servers:
                                • Check internet connection
                                • Try mobile data if on WiFi
                                • Verify API endpoint access
                            """.trimIndent()
                        }
                        else -> {
                            """
                                ❌ Connection Failed
                                
                                Error: ${t.message}
                                
                                Please try again.
                            """.trimIndent()
                        }
                    }

                    txtMonasteryDetails.text = errorMessage
                    android.util.Log.e("CameraActivity", "Failure: ${t.message}", t)
                    Toast.makeText(
                        this@CameraActivity,
                        "Request failed. Check connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            txtMonasteryDetails.text = """
                ❌ Unexpected Error
                
                ${e.message}
                
                Please restart and try again.
            """.trimIndent()
            android.util.Log.e("CameraActivity", "Exception: ${e.message}", e)
        }
    }

    /**
     * Optimized image compression to reduce size and processing time
     */
    private fun bitmapToBase64Optimized(bitmap: Bitmap): String {
        // Resize image if too large (max 1024px on longest side)
        val maxSize = 1024
        val scaledBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val ratio = minOf(
                maxSize.toFloat() / bitmap.width,
                maxSize.toFloat() / bitmap.height
            )
            val newWidth = (bitmap.width * ratio).toInt()
            val newHeight = (bitmap.height * ratio).toInt()
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        // Compress to 85% quality (good balance between size and quality)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val imageBytes = outputStream.toByteArray()

        android.util.Log.d("CameraActivity", "Image size: ${imageBytes.size / 1024}KB")

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission required to capture photos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}