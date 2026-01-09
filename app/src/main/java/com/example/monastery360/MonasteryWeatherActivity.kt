package com.example.monastery360

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.adapter.WeatherAdapter
import com.example.monastery360.model.MonasteryWeather
import com.example.monastery360.repository.MonasteryRepository
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MonasteryWeatherActivity : BaseActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var recyclerWeather: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtLastUpdated: TextView
    private lateinit var weatherAdapter: WeatherAdapter

    private val weatherList = mutableListOf<MonasteryWeather>()
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // ✅ WeatherAPI.com API Key
    // Get your FREE key from: https://www.weatherapi.com/signup.aspx
    // Paste your key below (NO WAITING - Works immediately!)
    private val API_KEY = "702f36adca5d4c92a5a44636253011" // Your weatherapi.com key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monastery_weather)

        initViews()
        setupRecyclerView()
        loadWeatherData()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackWeather)
        txtTitle = findViewById(R.id.txtWeatherTitle)
        recyclerWeather = findViewById(R.id.recyclerWeather)
        progressBar = findViewById(R.id.progressBarWeather)
        txtLastUpdated = findViewById(R.id.txtLastUpdated)

        btnBack.setOnClickListener {
            finish()
        }

        txtTitle.text = "Weather Updates"
        updateLastUpdatedTime()
    }

    private fun setupRecyclerView() {
        weatherAdapter = WeatherAdapter(weatherList)
        recyclerWeather.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(this@MonasteryWeatherActivity)
        }
    }

    private fun loadWeatherData() {
        progressBar.visibility = View.VISIBLE
        weatherList.clear()

        val monasteries = MonasteryRepository.getAllMonasteries()

        scope.launch {
            try {
                monasteries.forEach { monastery ->
                    val weather = fetchWeatherData(monastery.latitude, monastery.longitude, monastery.name)
                    weather?.let {
                        weatherList.add(it)
                        withContext(Dispatchers.Main) {
                            weatherAdapter.notifyDataSetChanged()
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (weatherList.isEmpty()) {
                        Toast.makeText(
                            this@MonasteryWeatherActivity,
                            "Unable to fetch weather data. Check internet connection.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@MonasteryWeatherActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun fetchWeatherData(lat: Double, lon: Double, name: String): MonasteryWeather? {
        return withContext(Dispatchers.IO) {
            try {
                // WeatherAPI.com endpoint
                val url = "https://api.weatherapi.com/v1/current.json?key=$API_KEY&q=$lat,$lon&aqi=no"
                val response = URL(url).readText()
                val jsonObject = JSONObject(response)

                // Parse WeatherAPI.com response
                val current = jsonObject.getJSONObject("current")
                val condition = current.getJSONObject("condition")

                val temp = current.getDouble("temp_c")
                val feelsLike = current.getDouble("feelslike_c")
                val humidity = current.getInt("humidity")
                val pressure = current.getInt("pressure_mb")
                val description = condition.getString("text")
                val iconUrl = "https:" + condition.getString("icon")
                val windSpeed = current.getDouble("wind_kph")
                val cloudiness = current.getInt("cloud")

                // Calculate trip suitability
                val tripAdvice = calculateTripAdvice(temp, humidity, windSpeed, cloudiness, description)

                MonasteryWeather(
                    monasteryName = name,
                    temperature = temp,
                    feelsLike = feelsLike,
                    humidity = humidity,
                    pressure = pressure,
                    description = description,
                    weatherIcon = iconUrl,
                    windSpeed = windSpeed,
                    cloudiness = cloudiness,
                    tripSuitability = tripAdvice.first,
                    tripAdviceText = tripAdvice.second,
                    lastUpdated = System.currentTimeMillis()
                )

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun calculateTripAdvice(
        temp: Double,
        humidity: Int,
        windSpeed: Double,
        cloudiness: Int,
        description: String
    ): Pair<String, String> {
        var score = 100
        val issues = mutableListOf<String>()

        // Temperature check (Ideal: 10-25°C)
        when {
            temp < 0 -> {
                score -= 40
                issues.add("बहुत ठंडा मौसम, गर्म कपड़े जरूरी • Very cold, heavy clothing required")
            }
            temp in 0.0..5.0 -> {
                score -= 30
                issues.add("ठंडा मौसम, गर्म परतें जरूरी • Cold weather, warm layers needed")
            }
            temp in 5.0..10.0 -> {
                score -= 15
                issues.add("सुहावना मौसम, जैकेट पहनें • Cool weather, jacket recommended")
            }
            temp in 10.0..25.0 -> {
                score += 10
                // Perfect temperature
            }
            temp in 25.0..30.0 -> {
                score -= 10
                issues.add("गर्म मौसम, पानी पीते रहें • Warm weather, stay hydrated")
            }
            temp > 30 -> {
                score -= 25
                issues.add("बहुत गर्म, पानी और धूप से बचाव • Very hot, carry water and sunscreen")
            }
        }

        // Humidity check
        when {
            humidity > 85 -> {
                score -= 20
                issues.add("उमस भरा मौसम • High humidity, may feel uncomfortable")
            }
            humidity < 30 -> {
                score -= 10
                issues.add("शुष्क हवा • Low humidity, lips may get dry")
            }
        }

        // Wind speed check
        when {
            windSpeed > 30 -> {
                score -= 25
                issues.add("तेज़ हवाएं, ट्रेकिंग मुश्किल • Strong winds, difficult for trekking")
            }
            windSpeed > 20 -> {
                score -= 15
                issues.add("हवादार, विंडप्रूफ जैकेट पहनें • Moderate winds, windproof jacket needed")
            }
        }

        // Weather description check
        val lowerDesc = description.lowercase()
        when {
            lowerDesc.contains("rain") || lowerDesc.contains("drizzle") || lowerDesc.contains("बारिश") -> {
                score -= 35
                issues.add("बारिश संभावित, छाता ले जाएं • Rain expected, carry umbrella")
            }
            lowerDesc.contains("thunder") || lowerDesc.contains("storm") -> {
                score -= 50
                issues.add("तूफान की चेतावनी, यात्रा टालें • Thunderstorm alert, avoid travel")
            }
            lowerDesc.contains("snow") || lowerDesc.contains("बर्फ") -> {
                score -= 30
                issues.add("बर्फबारी, सड़कें फिसलन भरी • Snowfall, roads may be slippery")
            }
            lowerDesc.contains("fog") || lowerDesc.contains("mist") || lowerDesc.contains("धुंध") -> {
                score -= 20
                issues.add("कम दृश्यता, ध्यान से गाड़ी चलाएं • Low visibility, drive carefully")
            }
            lowerDesc.contains("clear") || lowerDesc.contains("sunny") || lowerDesc.contains("साफ") -> {
                score += 15
                // Perfect weather
            }
            lowerDesc.contains("overcast") || lowerDesc.contains("cloudy") -> {
                score -= 5
                // Slight penalty for cloudy
            }
        }

        // Cloudiness check
        if (cloudiness > 80) {
            score -= 10
            issues.add("बादल छाए हैं, पहाड़ों का दृश्य सीमित • Overcast, limited mountain views")
        }

        // Determine suitability
        val suitability = when {
            score >= 80 -> "EXCELLENT"
            score >= 60 -> "GOOD"
            score >= 40 -> "MODERATE"
            score >= 20 -> "POOR"
            else -> "NOT_RECOMMENDED"
        }

        // Generate advice text
        val adviceText = when (suitability) {
            "EXCELLENT" -> "परफेक्ट मौसम! मठ यात्रा के लिए बिल्कुल सही। ${if (issues.isEmpty()) "साफ आसमान और सुहावना तापमान।" else issues.joinToString(" • ")} • Perfect weather for monastery visit!"
            "GOOD" -> "अच्छा मौसम! यात्रा के लिए उपयुक्त। ${if (issues.isEmpty()) "सुहावना मौसम अपेक्षित।" else issues.joinToString(" • ")} • Good conditions for visit."
            "MODERATE" -> "मध्यम स्थिति। ${issues.joinToString(" • ")}. योजना बनाकर चलें • Plan accordingly."
            "POOR" -> "चुनौतीपूर्ण मौसम। ${issues.joinToString(" • ")}. यात्रा स्थगित करें • Consider rescheduling."
            else -> "यात्रा की सलाह नहीं। ${issues.joinToString(" • ")}. बेहतर मौसम का इंतज़ार करें • Not recommended. Wait for better weather."
        }

        return Pair(suitability, adviceText)
    }

    private fun updateLastUpdatedTime() {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        txtLastUpdated.text = "Last updated: ${sdf.format(Date())}"
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}