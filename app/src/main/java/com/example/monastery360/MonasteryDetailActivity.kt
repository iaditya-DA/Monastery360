package com.example.monastery360

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.monastery360.adapter.ImageSliderAdapter
import com.example.monastery360.repository.MonasteryRepository
import com.example.monastery360.utils.LocaleHelper

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
            val result = textToSpeech?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                isTTSInitialized = false
            } else {
                isTTSInitialized = true
                // Set speech rate (1.0 is normal speed)
                textToSpeech?.setSpeechRate(0.9f)

                // Set up utterance progress listener
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

        btnBookVisit = findViewById(R.id.btnBookVisit)
    }

    private fun loadMonasteryData() {
        val monasteryName = intent.getStringExtra("MONASTERY_NAME") ?: return

        val monastery = MonasteryRepository.getAllMonasteries()
            .find { it.name == monasteryName } ?: return

        // Setup Image Slider
        val imageList = listOf(
            monastery.imageRes,
            monastery.imageRes,
            monastery.imageRes
        )

        val adapter = ImageSliderAdapter(imageList)
        viewPagerImages.adapter = adapter

        // Update image counter
        txtImageCounter.text = "1/${imageList.size}"

        viewPagerImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                txtImageCounter.text = "${position + 1}/${imageList.size}"
            }
        })

        // Set monastery data
        txtMonasteryName.text = monastery.name
        txtRating.text = monastery.rating.toString()
        txtReviews.text = "(${monastery.reviews} Review)"
        txtLocation.text = monastery.location
        txtAddress.text = monastery.address
        txtDescription.text = monastery.description
        txtPrice.text = monastery.price

        // ✅ Prepare audio text (description + location info)
        audioText = buildString {
            append("Welcome to ${monastery.name}. ")
            append("Located in ${monastery.location}. ")
            append("Address: ${monastery.address}. ")
            append(monastery.description)
        }

        // ✅ Calculate estimated duration (roughly 150 words per minute)
        val wordCount = audioText.split("\\s+".toRegex()).size
        estimatedDuration = (wordCount * 60) / 150 // seconds

        // Update UI with estimated duration
        txtAudioDuration.text = "${formatTime(estimatedDuration * 1000)} • English"
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
            // TODO: Implement share functionality
        }

        // ✅ Audio Play/Pause Button
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

        // ✅ SeekBar listener (Note: TTS doesn't support seeking, so we restart from beginning)
        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && isPlaying) {
                    // TTS doesn't support seeking, so we'll just restart
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
            // This is TTS so no actual download needed
        }

        btnReadMore.setOnClickListener {
            isExpanded = !isExpanded
            txtDescription.maxLines = if (isExpanded) Int.MAX_VALUE else 3
            btnReadMore.text = if (isExpanded) "Read less" else "Read more"
        }

        btnBookVisit.setOnClickListener {
            Toast.makeText(this, "Book Visit clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to booking screen
        }
    }

    // ✅ Audio Player Functions using Text-to-Speech
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

            // Start progress simulation
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
                    // Audio finished
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