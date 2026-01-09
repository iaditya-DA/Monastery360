package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.example.monastery360.adapter.ImageSliderAdapter
import com.example.monastery360.repository.MonasteryRepository
import java.util.*

class MonasteryDetailActivity : BaseActivity(), TextToSpeech.OnInitListener {

    // Image Slider
    private lateinit var viewPagerImages: ViewPager2
    private lateinit var txtImageCounter: TextView

    // Top Buttons
    private lateinit var btnBack: ImageView
    private lateinit var btnFavorite: ImageView
    private lateinit var btnShare: ImageView

    // Audio Guide
    private lateinit var btnPlayAudio: ImageView
    private lateinit var txtAudioDuration: TextView
    private lateinit var seekBarAudio: SeekBar
    private lateinit var txtCurrentTime: TextView
    private lateinit var txtTotalTime: TextView
    private lateinit var btnDownloadAudio: ImageView

    // Monastery Info
    private lateinit var txtMonasteryName: TextView
    private lateinit var txtRating: TextView
    private lateinit var txtReviews: TextView
    private lateinit var txtLocation: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtDescription: TextView
    private lateinit var btnReadMore: TextView
    private lateinit var txtPrice: TextView

    // Map Section
    private lateinit var mapCardView: CardView

    private lateinit var btnBookVisit: Button

    // State variables
    private var isFavorite = false
    private var isExpanded = false
    private var isPlaying = false

    // TextToSpeech for audio
    private var textToSpeech: TextToSpeech? = null
    private var isTTSInitialized = false
    private val handler = Handler(Looper.getMainLooper())

    // Store the text to be spoken
    private var audioText = ""
    private var estimatedDuration = 0
    private var currentProgress = 0

    // Location data
    private var latitude = 0.0
    private var longitude = 0.0
    private var monasteryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monastery_detail)

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        initViews()
        loadMonasteryData()
        setupListeners()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // ✅ Get current app language
            val currentLanguage = resources.configuration.locale.language

            val locale = when (currentLanguage) {
                "hi" -> Locale("hi", "IN")      // Hindi
                "ne" -> Locale("ne", "NP")      // Nepali
                else -> Locale.US               // English (default)
            }

            // ✅ Set language to TextToSpeech
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
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        runOnUiThread {
                            isPlaying = false
                            btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
                            seekBarAudio.progress = 0
                            txtCurrentTime.text = "0:00"
                            currentProgress = 0
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        runOnUiThread {
                            isPlaying = false
                            btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
                            Toast.makeText(this@MonasteryDetailActivity, "Error playing audio", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
            isTTSInitialized = false
        }
    }

    private fun initViews() {
        // Image Slider
        viewPagerImages = findViewById(R.id.viewPagerImages)
        txtImageCounter = findViewById(R.id.txtImageCounter)

        // Top Buttons
        btnBack = findViewById(R.id.btnBack)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnShare = findViewById(R.id.btnShare)

        // Audio Guide
        btnPlayAudio = findViewById(R.id.btnPlayAudio)
        txtAudioDuration = findViewById(R.id.txtAudioDuration)
        seekBarAudio = findViewById(R.id.seekBarAudio)
        txtCurrentTime = findViewById(R.id.txtCurrentTime)
        txtTotalTime = findViewById(R.id.txtTotalTime)
        btnDownloadAudio = findViewById(R.id.btnDownloadAudio)

        // Monastery Info
        txtMonasteryName = findViewById(R.id.txtMonasteryName)
        txtRating = findViewById(R.id.txtRating)
        txtReviews = findViewById(R.id.txtReviews)
        txtLocation = findViewById(R.id.txtLocation)
        txtAddress = findViewById(R.id.txtAddress)
        txtDescription = findViewById(R.id.txtDescription)
        btnReadMore = findViewById(R.id.btnReadMore)
        txtPrice = findViewById(R.id.txtPrice)

        // Map Section
        mapCardView = findViewById(R.id.mapCardView)

        btnBookVisit = findViewById(R.id.btnViewManuscript)
    }

    private fun loadMonasteryData() {
        val monasteryNameIntent = intent.getStringExtra("MONASTERY_NAME") ?: return

        val monastery = MonasteryRepository.getAllMonasteries()
            .find { it.name == monasteryNameIntent } ?: return

        val imageList = monastery.images

        val adapter = ImageSliderAdapter(imageList)
        viewPagerImages.adapter = adapter

        txtImageCounter.text = "1/${imageList.size}"

        viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                txtImageCounter.text = "${position + 1}/${imageList.size}"
            }
        })

        // ✅ Get translated strings
        val translatedName = getTranslatedMonasteryName(monastery.name)
        val translatedDesc = getTranslatedMonasteryDescription(monastery.name)
        val translatedLocation = getTranslatedMonasteryLocation(monastery.name)
        val translatedPrice = getTranslatedMonasteryPrice(monastery.name)

        // Set monastery data with translations
        monasteryName = monastery.name
        txtMonasteryName.text = translatedName
        txtRating.text = monastery.rating.toString()
        txtReviews.text = "(${monastery.reviews} Review)"
        txtLocation.text = translatedLocation
        txtAddress.text = monastery.address
        txtDescription.text = translatedDesc
        txtPrice.text = translatedPrice

        // Store location coordinates
        latitude = monastery.latitude
        longitude = monastery.longitude

        // Prepare audio text with translated content
        // Prepare audio text with translated content
        audioText = buildString {
            append("${getString(R.string.audio_welcome)} $translatedName. ")
            append("${getString(R.string.audio_located)} $translatedLocation. ")
            append("${getString(R.string.audio_address)}: ${monastery.address}. ")
            append(translatedDesc)
        }

        // Calculate estimated duration
        val wordCount = audioText.split("\\s+".toRegex()).size
        estimatedDuration = (wordCount * 60) / 150

        // ✅ Language based text
        val audioLanguage = when (resources.configuration.locale.language) {
            "hi" -> "हिंदी"
            "ne" -> "नेपाली"
            else -> "English"
        }

        txtAudioDuration.text = "${formatTime(estimatedDuration * 1000)} • $audioLanguage"
        txtTotalTime.text = formatTime(estimatedDuration * 1000)
        seekBarAudio.max = estimatedDuration
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.favorite
            )
            Toast.makeText(
                this,
                if (isFavorite) "Added to favorites" else "Removed from favorites",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnShare.setOnClickListener {
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
        }

        mapCardView.setOnClickListener {
            openFullScreenMap()
        }

        btnPlayAudio.setOnClickListener {
            if (!isTTSInitialized) {
                Toast.makeText(this, "Audio guide is still loading...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isPlaying) {
                pauseAudio()
            } else {
                playAudio()
            }
        }

        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && isPlaying) {
                    Toast.makeText(this@MonasteryDetailActivity, "Restarting audio...", Toast.LENGTH_SHORT).show()
                    stopAudio()
                    playAudio()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnDownloadAudio.setOnClickListener {
            Toast.makeText(this, "Audio guide saved for offline use", Toast.LENGTH_SHORT).show()
        }

        btnReadMore.setOnClickListener {
            isExpanded = !isExpanded
            txtDescription.maxLines = if (isExpanded) Int.MAX_VALUE else 3
            btnReadMore.text = if (isExpanded) "Read less" else "Read more"
        }

        btnBookVisit.setOnClickListener {
            // Open the manuscript list activity
            val intent = Intent(this, ManuscriptListActivity::class.java)

            // Optional: pass monastery name or ID if you want to filter manuscripts
            intent.putExtra("MONASTERY_NAME", monasteryName)

            startActivity(intent)
        }

    }

    private fun openFullScreenMap() {
        val intent = Intent(this, MonasteryMapsActivity::class.java)
        intent.putExtra("LATITUDE", latitude)
        intent.putExtra("LONGITUDE", longitude)
        intent.putExtra("MONASTERY_NAME", monasteryName)
        intent.putExtra("MONASTERY_ADDRESS", txtAddress.text.toString())
        startActivity(intent)
    }

    private fun playAudio() {
        if (!isTTSInitialized) {
            Toast.makeText(this, "Text-to-Speech not ready", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MONASTERY_AUDIO")

            textToSpeech?.speak(audioText, TextToSpeech.QUEUE_FLUSH, params, "MONASTERY_AUDIO")

            isPlaying = true
            btnPlayAudio.setImageResource(R.drawable.ic_pause_circle)

            updateSeekBar()

        } catch (e: Exception) {
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun pauseAudio() {
        textToSpeech?.stop()
        isPlaying = false
        btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
        handler.removeCallbacksAndMessages(null)
    }

    private fun stopAudio() {
        textToSpeech?.stop()
        isPlaying = false
        btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
        seekBarAudio.progress = 0
        txtCurrentTime.text = "0:00"
        currentProgress = 0
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying && currentProgress < estimatedDuration) {
                    currentProgress++
                    seekBarAudio.progress = currentProgress
                    txtCurrentTime.text = formatTime(currentProgress * 1000)

                    handler.postDelayed(this, 1000)
                } else if (currentProgress >= estimatedDuration) {
                    isPlaying = false
                    btnPlayAudio.setImageResource(R.drawable.ic_play_circle)
                    seekBarAudio.progress = 0
                    txtCurrentTime.text = "0:00"
                    currentProgress = 0
                }
            }
        }, 1000)
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    // ✅ TRANSLATED MONASTERY NAMES
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

    // ✅ TRANSLATED MONASTERY DESCRIPTIONS
    private fun getTranslatedMonasteryDescription(monasteryName: String): String {
        return when (monasteryName) {
            "Dubdi Monastery" -> getString(R.string.dubdi_description)
            "Enchey Monastery" -> getString(R.string.enchey_description)
            "Gonjang Monastery" -> getString(R.string.gonjang_description)
            "Kewzing Monastery" -> getString(R.string.kewzing_description)
            "Labrang Monastery" -> getString(R.string.labrang_description)
            "Phodong Monastery" -> getString(R.string.phodong_description)
            "Rinchenpong Monastery" -> getString(R.string.rinchenpong_description)
            "Rumtek Monastery" -> getString(R.string.rumtek_description)
            "Sangachoeling Monastery" -> getString(R.string.sangachoeling_description)
            "Tashiding Monastery" -> getString(R.string.tashiding_description)
            else -> "Description not available"
        }
    }

    // ✅ TRANSLATED MONASTERY LOCATIONS
    private fun getTranslatedMonasteryLocation(monasteryName: String): String {
        return when (monasteryName) {
            "Dubdi Monastery" -> getString(R.string.dubdi_location)
            "Enchey Monastery" -> getString(R.string.enchey_location)
            "Gonjang Monastery" -> getString(R.string.gonjang_location)
            "Kewzing Monastery" -> getString(R.string.kewzing_location)
            "Labrang Monastery" -> getString(R.string.labrang_location)
            "Phodong Monastery" -> getString(R.string.phodong_location)
            "Rinchenpong Monastery" -> getString(R.string.rinchenpong_location)
            "Rumtek Monastery" -> getString(R.string.rumtek_location)
            "Sangachoeling Monastery" -> getString(R.string.sangachoeling_location)
            "Tashiding Monastery" -> getString(R.string.tashiding_location)
            else -> "Location unknown"
        }
    }

    // ✅ TRANSLATED MONASTERY PRICES
    private fun getTranslatedMonasteryPrice(monasteryName: String): String {
        return when (monasteryName) {
            "Dubdi Monastery" -> getString(R.string.dubdi_price)
            "Enchey Monastery" -> getString(R.string.enchey_price)
            "Gonjang Monastery" -> getString(R.string.gonjang_price)
            "Kewzing Monastery" -> getString(R.string.kewzing_price)
            "Labrang Monastery" -> getString(R.string.labrang_price)
            "Phodong Monastery" -> getString(R.string.phodong_price)
            "Rinchenpong Monastery" -> getString(R.string.rinchenpong_price)
            "Rumtek Monastery" -> getString(R.string.rumtek_price)
            "Sangachoeling Monastery" -> getString(R.string.sangachoeling_price)
            "Tashiding Monastery" -> getString(R.string.tashiding_price)
            else -> "Price unknown"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pauseAudio()
        }
    }
}