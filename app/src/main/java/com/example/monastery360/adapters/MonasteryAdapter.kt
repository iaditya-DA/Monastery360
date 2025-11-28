package com.example.monastery360

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.model.Monastery

class MonasteryAdapter(
    private val list: List<Monastery>,
    private val isVertical: Boolean = false,
    private val onItemClick: (Monastery) -> Unit = {}
) : RecyclerView.Adapter<MonasteryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgMonastery)
        val name: TextView = itemView.findViewById(R.id.txtName)
        val distance: TextView = itemView.findViewById(R.id.txtDistance)
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
        holder.img.setImageResource(monastery.imageRes)
        holder.name.text = monastery.name
        holder.distance.text = monastery.distance

        holder.itemView.setOnClickListener {
            onItemClick(monastery)
        }
    }

    override fun getItemCount(): Int = list.size
}
