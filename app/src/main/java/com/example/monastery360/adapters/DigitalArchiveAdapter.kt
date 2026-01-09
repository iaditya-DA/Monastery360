package com.example.monastery360.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monastery360.R
import com.example.monastery360.model.DigitalArchive

class DigitalArchiveAdapter(
    private val list: List<DigitalArchive>,
    private val onItemClick: (DigitalArchive) -> Unit
) : RecyclerView.Adapter<DigitalArchiveAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtManuscriptTitle)
        val txtType: TextView = view.findViewById(R.id.txtManuscriptType)
        val imgPreview: ImageView = view.findViewById(R.id.imgManuscript)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manuscript, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.txtTitle.text = item.title
        holder.txtType.text = item.type

        Glide.with(holder.itemView.context)
            .load(item.images.first())
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imgPreview)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size
}
