package com.example.monastery360

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.monastery360.repository.MonasteryRepository
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PlanningActivity : BaseActivity() {

    private lateinit var backButton: ImageView
    private lateinit var planButton: Button
    private lateinit var districtSpinner: Spinner
    private lateinit var specialRequests: EditText
    private lateinit var btn2days: Button
    private lateinit var btn3days: Button
    private lateinit var btn4days: Button
    private lateinit var btn5days: Button
    private lateinit var btnSolo: Button
    private lateinit var btnCouple: Button
    private lateinit var btnFamily: Button
    private lateinit var btnCultural: Button
    private lateinit var btnAdventure: Button
    private lateinit var btnNature: Button
    private lateinit var btnPhotography: Button

    private var selectedDays = 2
    private var selectedTravelType = "Solo"
    private val selectedInterests = mutableSetOf<String>()
    private var selectedDistrict = "All Districts"

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // ---- Your OpenAI API Key ----
    private val OPENAI_API_KEY = "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"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning)

        initViews()
        setupSpinner()
        setupListeners()

        selectDay(2, btn2days, btn3days, btn4days, btn5days)
        selectType("Solo", btnSolo, btnCouple, btnFamily)
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        planButton = findViewById(R.id.planMyTripBtn)
        districtSpinner = findViewById(R.id.districtSpinner)
        specialRequests = findViewById(R.id.specialRequests)
        btn2days = findViewById(R.id.btn2days)
        btn3days = findViewById(R.id.btn3days)
        btn4days = findViewById(R.id.btn4days)
        btn5days = findViewById(R.id.btn5days)
        btnSolo = findViewById(R.id.btnSolo)
        btnCouple = findViewById(R.id.btnCouple)
        btnFamily = findViewById(R.id.btnFamily)
        btnCultural = findViewById(R.id.btnCultural)
        btnAdventure = findViewById(R.id.btnAdventure)
        btnNature = findViewById(R.id.btnNature)
        btnPhotography = findViewById(R.id.btnPhotography)
    }

    private fun setupSpinner() {
        val districts = arrayOf(
            "All Districts", "East Sikkim (Gangtok)", "West Sikkim (Pelling)",
            "North Sikkim (Mangan)", "South Sikkim (Namchi)"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        districtSpinner.adapter = adapter

        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedDistrict = districts[pos]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        btn2days.setOnClickListener { selectDay(2, btn2days, btn3days, btn4days, btn5days) }
        btn3days.setOnClickListener { selectDay(3, btn3days, btn2days, btn4days, btn5days) }
        btn4days.setOnClickListener { selectDay(4, btn4days, btn2days, btn3days, btn5days) }
        btn5days.setOnClickListener { selectDay(5, btn5days, btn2days, btn3days, btn4days) }

        btnSolo.setOnClickListener { selectType("Solo", btnSolo, btnCouple, btnFamily) }
        btnCouple.setOnClickListener { selectType("Couple", btnCouple, btnSolo, btnFamily) }
        btnFamily.setOnClickListener { selectType("Family", btnFamily, btnSolo, btnCouple) }

        btnCultural.setOnClickListener { toggleInterest("Cultural", btnCultural) }
        btnAdventure.setOnClickListener { toggleInterest("Adventure", btnAdventure) }
        btnNature.setOnClickListener { toggleInterest("Nature", btnNature) }
        btnPhotography.setOnClickListener { toggleInterest("Photography", btnPhotography) }

        planButton.setOnClickListener {
            if (OPENAI_API_KEY.isEmpty()) {
                showApiKeyMissingDialog()
            } else {
                generateTripPlan()
            }
        }
    }

    private fun selectDay(days: Int, selected: Button, vararg others: Button) {
        selectedDays = days
        selected.setBackgroundResource(R.drawable.btn_selected)
        selected.setTextColor(Color.WHITE)

        others.forEach {
            it.setBackgroundResource(R.drawable.btn_unselected)
            it.setTextColor(Color.BLACK)  // 👈 previously grey tha, ab visible rahega
        }
    }


    private fun selectType(type: String, selected: Button, vararg others: Button) {
        selectedTravelType = type
        selected.setBackgroundResource(R.drawable.btn_selected)
        selected.setTextColor(Color.WHITE)

        others.forEach {
            it.setBackgroundResource(R.drawable.btn_unselected)
            it.setTextColor(Color.BLACK)  // 👈 fixed
        }
    }


    private fun toggleInterest(interest: String, button: Button) {
        if (selectedInterests.contains(interest)) {
            selectedInterests.remove(interest)
            button.setBackgroundResource(R.drawable.btn_unselected)
            button.setTextColor(Color.BLACK) // 👈 unselected visible
        } else {
            selectedInterests.add(interest)
            button.setBackgroundResource(R.drawable.btn_selected)
            button.setTextColor(Color.WHITE) // 👈 selected clear
        }
    }


    private fun showApiKeyMissingDialog() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ API Key Missing")
            .setMessage("Please add your OpenAI API Key inside PlanningActivity.kt")
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .show()
    }

    private fun generateTripPlan() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("⏳ Creating Itinerary")
            .setMessage("AI is preparing your monastery trip…")
            .setCancelable(false)
            .create()
        dialog.show()

        scope.launch(Dispatchers.IO) {
            try {
                val json = callOpenAIAPI()

                withContext(Dispatchers.Main) {
                    dialog.dismiss()

                    if (json.isNotEmpty() && json.startsWith("{")) {
                        try {
                            JSONObject(json)

                            val intent = Intent(this@PlanningActivity, ItineraryActivity::class.java)
                            intent.putExtra("ITINERARY_JSON", json)
                            intent.putExtra("DAYS", selectedDays)
                            intent.putExtra("DISTRICT", selectedDistrict)
                            intent.putExtra("TRAVEL_TYPE", selectedTravelType)
                            startActivity(intent)

                        } catch (e: Exception) {
                            Toast.makeText(this@PlanningActivity, "Invalid JSON!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@PlanningActivity, "Failed to generate plan!", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(this@PlanningActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("OpenAIError", "error", e)
                }
            }
        }
    }

    private suspend fun callOpenAIAPI(): String = withContext(Dispatchers.IO) {
        try {
            val monasteries = MonasteryRepository.getAllMonasteries()
            val filtered = if (selectedDistrict == "All Districts") monasteries else {
                monasteries.filter {
                    it.district.contains(selectedDistrict.substringBefore("(").trim(), true)
                }
            }
            val names = filtered.take(15).joinToString(", ") { it.name }
            val prompt = buildPrompt(names)

            val apiUrl = "https://api.openai.com/v1/chat/completions"

            val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $OPENAI_API_KEY")
                doOutput = true
            }

            val body = JSONObject().apply {
                put("model", "gpt-4-0613")
                put("messages", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                }))
            }

            OutputStreamWriter(connection.outputStream).use {
                it.write(body.toString())
            }

            val code = connection.responseCode
            val response = if (code == 200) {
                connection.inputStream.bufferedReader().readText()
            } else {
                val err = connection.errorStream?.bufferedReader()?.readText()
                Log.e("OpenAIAPI", "Error: $err")
                throw Exception("API error: $code")
            }

            // Extract JSON from OpenAI response
            val choices = JSONObject(response).getJSONArray("choices")
            val content = choices.getJSONObject(0).getJSONObject("message").getString("content")
            return@withContext content.trim()

        } catch (e: Exception) {
            Log.e("OpenAIAPI", "Exception", e)
            throw e
        }
    }

    private fun buildPrompt(monasteryNames: String): String {
        val interests = if (selectedInterests.isEmpty())
            "Cultural and spiritual exploration"
        else selectedInterests.joinToString(", ")

        val notes = specialRequests.text.toString().ifEmpty { "No special requests." }

        return """
Create a $selectedDays-day monastery tour itinerary for $selectedDistrict in Sikkim.

TRIP DETAILS:
- Duration: $selectedDays days
- Travel Type: $selectedTravelType
- Interests: $interests
- Notes: $notes
- Monasteries: $monasteryNames

Return only RAW JSON:
{
  "title": "",
  "overview": "",
  "days": [
    {
      "day": 1,
      "title": "",
      "activities": [
        {
          "time": "",
          "location": "",
          "description": "",
          "duration": ""
        }
      ],
      "meals": "",
      "accommodation": ""
    }
  ],
  "tips": [],
  "budget": {
    "accommodation": "",
    "food": "",
    "transport": "",
    "total": ""
  }
}
""".trimIndent()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
