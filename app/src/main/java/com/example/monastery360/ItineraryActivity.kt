package com.example.monastery360

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class ItineraryActivity : BaseActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var recyclerDays: RecyclerView
    private lateinit var txtTravelTips: TextView
    private lateinit var txtBudget: TextView
    private lateinit var btnShare: Button

    private var itineraryJson = ""
    private var days = 2
    private var district = ""
    private var travelType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)

        // Get data from intent
        itineraryJson = intent.getStringExtra("ITINERARY_JSON") ?: "{}"
        days = intent.getIntExtra("DAYS", 2)
        district = intent.getStringExtra("DISTRICT") ?: "Sikkim"
        travelType = intent.getStringExtra("TRAVEL_TYPE") ?: "Solo"

        initViews()
        parseAndDisplayItinerary()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackItinerary)
        txtTitle = findViewById(R.id.txtItineraryTitle)
        txtSubtitle = findViewById(R.id.txtItinerarySubtitle)
        recyclerDays = findViewById(R.id.recyclerDays)
        txtTravelTips = findViewById(R.id.txtTravelTips)
        txtBudget = findViewById(R.id.txtBudgetInfo)
        btnShare = findViewById(R.id.btnShareItinerary)

        btnBack.setOnClickListener { finish() }
        btnShare.setOnClickListener { shareItinerary() }

        txtTitle.text = "$days-Day Monastery Tour"
        txtSubtitle.text = "$district • $travelType"
    }

    private fun parseAndDisplayItinerary() {
        try {
            // Clean markdown if present
            val cleanJson = itineraryJson.replace("```json", "").replace("```", "").trim()
            val jsonObject = JSONObject(cleanJson)

            // --- Days ---
            if (jsonObject.has("days")) {
                val daysArray = jsonObject.getJSONArray("days")
                val dayList = mutableListOf<DayItinerary>()

                for (i in 0 until daysArray.length()) {
                    val dayObj = daysArray.getJSONObject(i)
                    val dayTitle = "Day ${dayObj.getInt("day")}: ${dayObj.optString("title","")}"

                    val activities = mutableListOf<Activity>()
                    val actArray = dayObj.getJSONArray("activities")
                    for (j in 0 until actArray.length()) {
                        val act = actArray.getJSONObject(j)
                        activities.add(
                            Activity(
                                time = act.optString("time",""),
                                activity = act.optString("description",""),
                                location = act.optString("location",""),
                                duration = act.optString("duration",""),
                                notes = ""
                            )
                        )
                    }
                    dayList.add(DayItinerary(dayTitle, activities))
                }

                val adapter = DayItineraryAdapter(dayList)
                recyclerDays.adapter = adapter
                recyclerDays.layoutManager = LinearLayoutManager(this)
            }

            // --- Travel Tips ---
            if (jsonObject.has("tips")) {
                val tipsArray = jsonObject.getJSONArray("tips")
                val tipsText = StringBuilder("🎒 Travel Tips:\n\n")
                for (i in 0 until tipsArray.length()) {
                    tipsText.append("• ${tipsArray.getString(i)}\n")
                }
                txtTravelTips.text = tipsText.toString()
            }

            // --- Budget ---
            if (jsonObject.has("budget")) {
                val budgetObj = jsonObject.getJSONObject("budget")
                val budgetText = buildString {
                    append("💰 Estimated Budget:\n\n")
                    if (budgetObj.has("accommodation")) append("Accommodation: ${budgetObj.getString("accommodation")}\n")
                    if (budgetObj.has("food")) append("Food: ${budgetObj.getString("food")}\n")
                    if (budgetObj.has("transport")) append("Transport: ${budgetObj.getString("transport")}\n")
                    if (budgetObj.has("total")) append("Total: ${budgetObj.getString("total")}")
                }
                txtBudget.text = budgetText
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing itinerary: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun shareItinerary() {
        // TODO: Implement share functionality
        Toast.makeText(this, "Share feature coming soon!", Toast.LENGTH_SHORT).show()
    }

    // --- Data classes ---
    data class DayItinerary(val day: String, val activities: List<Activity>)
    data class Activity(
        val time: String,
        val activity: String,
        val location: String,
        val duration: String,
        val notes: String
    )

    // --- RecyclerView Adapter ---
    inner class DayItineraryAdapter(private val days: List<DayItinerary>) :
        RecyclerView.Adapter<DayItineraryAdapter.DayViewHolder>() {

        inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val dayTitle: TextView = view.findViewById(R.id.txtDayTitle)
            val activitiesContainer: LinearLayout = view.findViewById(R.id.activitiesContainer)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): DayViewHolder {
            val view = layoutInflater.inflate(R.layout.item_day_itinerary, parent, false)
            return DayViewHolder(view)
        }

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
            val day = days[position]
            holder.dayTitle.text = day.day
            holder.activitiesContainer.removeAllViews()

            day.activities.forEach { activity ->
                val activityView = layoutInflater.inflate(R.layout.item_activity, holder.activitiesContainer, false)

                activityView.findViewById<TextView>(R.id.txtActivityTime).text = activity.time
                activityView.findViewById<TextView>(R.id.txtActivityName).text = activity.activity
                activityView.findViewById<TextView>(R.id.txtActivityLocation).text = "📍 ${activity.location}"
                activityView.findViewById<TextView>(R.id.txtActivityDuration).text = "⏱️ ${activity.duration}"

                holder.activitiesContainer.addView(activityView)
            }
        }

        override fun getItemCount() = days.size
    }
}
