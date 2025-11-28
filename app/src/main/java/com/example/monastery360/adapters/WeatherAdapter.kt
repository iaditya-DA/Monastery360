package com.example.monastery360.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monastery360.R
import com.example.monastery360.model.MonasteryWeather

class WeatherAdapter(
    private val weatherList: List<MonasteryWeather>
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardContainer: CardView = view.findViewById(R.id.cardWeatherItem)
        val txtMonasteryName: TextView = view.findViewById(R.id.txtWeatherMonasteryName)
        val imgWeatherIcon: ImageView = view.findViewById(R.id.imgWeatherIcon)
        val txtTemperature: TextView = view.findViewById(R.id.txtTemperature)
        val txtFeelsLike: TextView = view.findViewById(R.id.txtFeelsLike)
        val txtWeatherDesc: TextView = view.findViewById(R.id.txtWeatherDescription)
        val txtHumidity: TextView = view.findViewById(R.id.txtHumidity)
        val txtWindSpeed: TextView = view.findViewById(R.id.txtWindSpeed)
        val txtCloudiness: TextView = view.findViewById(R.id.txtCloudiness)
        val txtTripStatus: TextView = view.findViewById(R.id.txtTripStatus)
        val txtTripAdvice: TextView = view.findViewById(R.id.txtTripAdvice)
        val viewStatusIndicator: View = view.findViewById(R.id.viewStatusIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monastery_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]

        holder.txtMonasteryName.text = weather.monasteryName
        holder.txtTemperature.text = "${weather.temperature.toInt()}°C"
        holder.txtFeelsLike.text = "Feels like ${weather.feelsLike.toInt()}°C"
        holder.txtWeatherDesc.text = weather.description
        holder.txtHumidity.text = "${weather.humidity}%"
        holder.txtWindSpeed.text = "${weather.windSpeed.toInt()} km/h"
        holder.txtCloudiness.text = "${weather.cloudiness}%"
        holder.txtTripAdvice.text = weather.tripAdviceText

        // Load weather icon (WeatherAPI.com provides full URL)
        Glide.with(holder.itemView.context)
            .load(weather.weatherIcon) // Already has full URL from API
            .placeholder(R.drawable.ic_weather_cloud)
            .error(R.drawable.ic_weather_cloud)
            .into(holder.imgWeatherIcon)

        // Set trip status with color coding
        when (weather.tripSuitability) {
            "EXCELLENT" -> {
                holder.txtTripStatus.text = "✓ Excellent for Trip"
                holder.txtTripStatus.setTextColor(Color.parseColor("#4CAF50"))
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#4CAF50"))
                holder.cardContainer.setCardBackgroundColor(Color.parseColor("#F1F8F4"))
            }
            "GOOD" -> {
                holder.txtTripStatus.text = "✓ Good for Trip"
                holder.txtTripStatus.setTextColor(Color.parseColor("#8BC34A"))
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#8BC34A"))
                holder.cardContainer.setCardBackgroundColor(Color.parseColor("#F7F9F4"))
            }
            "MODERATE" -> {
                holder.txtTripStatus.text = "⚠ Moderate Conditions"
                holder.txtTripStatus.setTextColor(Color.parseColor("#FF9800"))
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#FF9800"))
                holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFF8F0"))
            }
            "POOR" -> {
                holder.txtTripStatus.text = "⚠ Poor Conditions"
                holder.txtTripStatus.setTextColor(Color.parseColor("#FF5722"))
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#FF5722"))
                holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFF3F0"))
            }
            "NOT_RECOMMENDED" -> {
                holder.txtTripStatus.text = "✗ Not Recommended"
                holder.txtTripStatus.setTextColor(Color.parseColor("#F44336"))
                holder.viewStatusIndicator.setBackgroundColor(Color.parseColor("#F44336"))
                holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
            }
        }
    }

    override fun getItemCount(): Int = weatherList.size
}