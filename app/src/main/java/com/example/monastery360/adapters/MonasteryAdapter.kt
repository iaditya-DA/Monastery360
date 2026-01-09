package com.example.monastery360

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monastery360.manager.FavoritesManager
import com.example.monastery360.model.Monastery
import java.text.DecimalFormat

class MonasteryAdapter(
    private val list: List<Monastery>,
    private val isVertical: Boolean = false,
    private val onItemClick: (Monastery) -> Unit = {}
) : RecyclerView.Adapter<MonasteryAdapter.ViewHolder>() {

    private var distanceMap: Map<String, Float> = emptyMap()

    fun updateDistances(userLocation: Location) {
        val newDistanceMap = mutableMapOf<String, Float>()
        val monasteryLocation = Location("Monastery")

        list.forEach { monastery ->
            monasteryLocation.latitude = monastery.latitude
            monasteryLocation.longitude = monastery.longitude

            val distanceInMeters = userLocation.distanceTo(monasteryLocation)
            val distanceInKm = distanceInMeters / 1000f

            newDistanceMap[monastery._id] = distanceInKm
        }
        this.distanceMap = newDistanceMap
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgMonastery)
        val name: TextView = itemView.findViewById(R.id.txtName)
        val distance: TextView = itemView.findViewById(R.id.txtDistance)
        val favIcon: ImageView = itemView.findViewById(R.id.favIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (isVertical) {
            R.layout.item_monastery_vertical
        } else {
            R.layout.item_monastery
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)

        if (!isVertical) {
            val displayMetrics = parent.context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val cardWidth = (screenWidth * 0.85).toInt()
            view.layoutParams.width = cardWidth
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monastery = list[position]
        val monasteryName = monastery.name

        // 🔥 Image Load Logic
        if (monastery.imageRes != 0) {
            Glide.with(holder.itemView.context)
                .load(monastery.imageRes)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.img)
        } else if (monastery.images.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(monastery.images[0])
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.img)
        } else {
            holder.img.setImageResource(R.drawable.ic_placeholder)
        }

        // ✅ TRANSLATED MONASTERY NAME
        val translatedName = getTranslatedMonasteryName(holder.itemView.context, monasteryName)
        holder.name.text = translatedName

        // Distance Logic
        val distanceInKm = distanceMap[monastery._id]
        if (distanceInKm != null && distanceInKm > 0) {
            val df = DecimalFormat("#.#")
            holder.distance.text = "${df.format(distanceInKm)} km"
            holder.distance.visibility = View.VISIBLE
        } else {
            holder.distance.visibility = View.GONE
        }

        // ❤️ Favorite icon
        val isFav = FavoritesManager.isFavorite(monasteryName)
        holder.favIcon.setImageResource(
            if (isFav) R.drawable.ic_favorite_filled
            else R.drawable.favorite
        )

        holder.favIcon.setOnClickListener {
            if (FavoritesManager.isFavorite(monasteryName)) {
                FavoritesManager.removeFavorite(monasteryName)
                holder.favIcon.setImageResource(R.drawable.favorite)
            } else {
                FavoritesManager.addFavorite(monasteryName)
                holder.favIcon.setImageResource(R.drawable.ic_favorite_filled)
            }
            notifyItemChanged(position)
        }

        // Item click listener
        holder.itemView.setOnClickListener {
            onItemClick(monastery)
        }
    }

    override fun getItemCount(): Int = list.size

    // ✅ TRANSLATED MONASTERY NAMES
    private fun getTranslatedMonasteryName(context: android.content.Context, monasteryName: String): String {
        return when (monasteryName) {
            "Dubdi Monastery" -> context.getString(R.string.dubdi_name)
            "Enchey Monastery" -> context.getString(R.string.enchey_name)
            "Gonjang Monastery" -> context.getString(R.string.gonjang_name)
            "Kewzing Monastery" -> context.getString(R.string.kewzing_name)
            "Labrang Monastery" -> context.getString(R.string.labrang_name)
            "Phodong Monastery" -> context.getString(R.string.phodong_name)
            "Rinchenpong Monastery" -> context.getString(R.string.rinchenpong_name)
            "Rumtek Monastery" -> context.getString(R.string.rumtek_name)
            "Sangachoeling Monastery" -> context.getString(R.string.sangachoeling_name)
            "Tashiding Monastery" -> context.getString(R.string.tashiding_name)
            else -> monasteryName
        }
    }
}