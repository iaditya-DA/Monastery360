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

    // Yeh map monastery ID aur uski doori (in KM) store karega
    private var distanceMap: Map<String, Float> = emptyMap()

    /**
     * MainActivity/Fragment se call karne ke liye function.
     * Yeh user ki location lekar har monastery se doori calculate karta hai
     * aur RecyclerView ko refresh kar deta hai.
     */
    fun updateDistances(userLocation: Location) {
        val newDistanceMap = mutableMapOf<String, Float>()
        val monasteryLocation = Location("Monastery") // Reusable Location object

        list.forEach { monastery ->
            // Monastery ke Lat/Lng se ek Location object banayein
            monasteryLocation.latitude = monastery.latitude
            monasteryLocation.longitude = monastery.longitude

            // User ki location se doori calculate karein (meters mein)
            val distanceInMeters = userLocation.distanceTo(monasteryLocation)

            // Meters ko Kilometers mein convert karein
            val distanceInKm = distanceInMeters / 1000f

            // Map mein store karein (monastery._id as key)
            newDistanceMap[monastery._id] = distanceInKm
        }
        this.distanceMap = newDistanceMap
        // RecyclerView ko refresh karne ke liye notify karein
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgMonastery)
        val name: TextView = itemView.findViewById(R.id.txtName)
        val distance: TextView = itemView.findViewById(R.id.txtDistance)
        val favIcon: ImageView = itemView.findViewById(R.id.favIcon)   // ❤️
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (isVertical) {
            R.layout.item_monastery_vertical
        } else {
            R.layout.item_monastery
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)

        // Horizontal card width adjust
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
        val monasteryName = monastery.name // Monastery ka naam store karo

        // 🔥 Image Load Logic (supports BOTH drawable & URL)
        if (monastery.imageRes != 0) {
            // Load drawable
            Glide.with(holder.itemView.context)
                .load(monastery.imageRes)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.img)
        } else if (monastery.images.isNotEmpty()) {
            // Load first URL from Cloudinary
            Glide.with(holder.itemView.context)
                .load(monastery.images[0])
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.img)
        } else {
            // Default image if nothing found
            holder.img.setImageResource(R.drawable.ic_placeholder)
        }

        // ---== TEXT AND DISTANCE LOGIC ==---
        holder.name.text = monasteryName

        // Distance ko map se nikal kar format karein (monastery ke _id se)
        val distanceInKm = distanceMap[monastery._id]
        if (distanceInKm != null && distanceInKm > 0) {
            // Doori ko format karein (e.g., "12.3 km")
            val df = DecimalFormat("#.#")
            holder.distance.text = "${df.format(distanceInKm)} km"
            holder.distance.visibility = View.VISIBLE
        } else {
            // Agar doori calculate nahi hui hai, to hide kar do
            holder.distance.visibility = View.GONE
        }

        // ❤️ Favorite icon - CHECK FAVORITESMANAGER (monastery name se)
        val isFav = FavoritesManager.isFavorite(monasteryName)
        holder.favIcon.setImageResource(
            if (isFav) R.drawable.ic_favorite_filled
            else R.drawable.favorite
        )

        // ❤️ Favorite icon Click Listener - UPDATED WITH FAVORITESMANAGER
        holder.favIcon.setOnClickListener {
            if (FavoritesManager.isFavorite(monasteryName)) {
                // Remove from favorites
                FavoritesManager.removeFavorite(monasteryName)
                holder.favIcon.setImageResource(R.drawable.favorite)
            } else {
                // Add to favorites
                FavoritesManager.addFavorite(monasteryName)
                holder.favIcon.setImageResource(R.drawable.ic_favorite_filled)
            }
            // Sirf yeh item update ho
            notifyItemChanged(position)
        }

        // Item click listener
        holder.itemView.setOnClickListener {
            onItemClick(monastery)
        }
    }


    override fun getItemCount(): Int = list.size
}