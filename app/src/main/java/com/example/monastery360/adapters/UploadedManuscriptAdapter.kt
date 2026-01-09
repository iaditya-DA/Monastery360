package com.example.monastery360.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.R
import com.example.monastery360.database.ManuscriptEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadedManuscriptAdapter(
    private val manuscripts: List<ManuscriptEntity>,
    private val onItemClick: (ManuscriptEntity) -> Unit,
    private val onDeleteClick: (ManuscriptEntity) -> Unit
) : RecyclerView.Adapter<UploadedManuscriptAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtFilename: TextView = itemView.findViewById(R.id.txtManuscriptName)
        private val txtDate: TextView = itemView.findViewById(R.id.txtManuscriptDate)
        private val txtLanguage: TextView = itemView.findViewById(R.id.txtManuscriptLanguage)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteManuscript)

        fun bind(manuscript: ManuscriptEntity) {
            // Set filename
            txtFilename.text = manuscript.filename

            // Format and set date
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateString = sdf.format(Date(manuscript.timestamp))
            txtDate.text = dateString

            // Set language
            txtLanguage.text = "Language: ${getLanguageName(manuscript.targetLanguage)}"

            // Item click listener
            itemView.setOnClickListener {
                onItemClick(manuscript)
            }

            // Delete button click listener
            btnDelete.setOnClickListener {
                onDeleteClick(manuscript)
            }
        }

        /**
         * Convert language code to language name
         */
        private fun getLanguageName(code: String): String {
            return when (code) {
                "en" -> "English"
                "hi" -> "Hindi"
                "sa" -> "Sanskrit"
                "es" -> "Spanish"
                "fr" -> "French"
                "de" -> "German"
                "it" -> "Italian"
                "pt" -> "Portuguese"
                "ru" -> "Russian"
                "ja" -> "Japanese"
                "ko" -> "Korean"
                "zh" -> "Chinese"
                "ar" -> "Arabic"
                "bn" -> "Bengali"
                "la" -> "Latin"
                "el" -> "Greek"
                else -> code.uppercase()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_uploaded_manuscript, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(manuscripts[position])
    }

    override fun getItemCount() = manuscripts.size
}