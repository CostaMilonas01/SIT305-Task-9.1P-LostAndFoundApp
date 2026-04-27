package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdvertListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adverts: ArrayList<Advert>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 30, 30, 30)

        val categorySpinner = Spinner(this)
        val categories = arrayOf("All", "Electronics", "Pets", "Wallets", "Keys", "Clothing", "Other")
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        val filterButton = Button(this)
        filterButton.text = "Filter"

        listView = ListView(this)

        layout.addView(categorySpinner)
        layout.addView(filterButton)
        layout.addView(listView)

        setContentView(layout)

        loadAllAdverts()

        filterButton.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()

            if (selectedCategory == "All") {
                loadAllAdverts()
            } else {
                adverts = dbHelper.getAdvertsByCategory(selectedCategory)
                displayAdverts()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedAdvert = adverts[position]
            val intent = Intent(this, AdvertDetailActivity::class.java)
            intent.putExtra("advertId", selectedAdvert.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllAdverts()
    }

    private fun loadAllAdverts() {
        adverts = dbHelper.getAllAdverts()
        displayAdverts()
    }

    private fun displayAdverts() {
        val advertNames = adverts.map {
            "${it.type}: ${it.name} - ${it.category}\nPosted: ${it.timestamp}"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, advertNames)
        listView.adapter = adapter
    }
}