package com.example.monastery360

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monastery360.model.Monastery
import com.example.monastery360.repository.MonasteryRepository
import com.example.monastery360.utils.LocaleHelper



class AllMonasteriesActivity : BaseActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_monasteries)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAllMonasteries)

        // Setup RecyclerView - VERTICAL mode with click listener
        val adapter = MonasteryAdapter(
            list = MonasteryRepository.getAllMonasteries(),
            isVertical = true,
            onItemClick = { monastery ->  // ✅ Add click handler
                openMonasteryDetail(monastery)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllMonasteriesActivity)
            this.adapter = adapter
            setHasFixedSize(true)
        }
    }

    // ✅ Function to open detail page
    private fun openMonasteryDetail(monastery: Monastery) {
        val intent = Intent(this, MonasteryDetailActivity::class.java)
        intent.putExtra("MONASTERY_NAME", monastery.name)
        startActivity(intent)
    }


}