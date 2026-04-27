package com.example.lostfoundapp

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AdvertDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var advertId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)
        advertId = intent.getIntExtra("advertId", -1)

        val advert = dbHelper.getAdvertById(advertId)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 40, 40, 40)

        if (advert == null) {
            val errorText = TextView(this)
            errorText.text = "Advert not found"
            layout.addView(errorText)
            setContentView(layout)
            return
        }

        val imageView = ImageView(this)
        imageView.minimumHeight = 400


        try {
            if (advert.imageUri.isNotEmpty()) {
                imageView.setImageURI(Uri.parse(advert.imageUri))
            }
        } catch (e: Exception) {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        val detailsText = TextView(this)
        detailsText.text = """
            Type: ${advert.type}
            Category: ${advert.category}
            Name: ${advert.name}
            Phone: ${advert.phone}
            Description: ${advert.description}
            Date: ${advert.date}
            Location: ${advert.location}
            Posted: ${advert.timestamp}
        """.trimIndent()

        val removeButton = Button(this)
        removeButton.text = "Remove"

        layout.addView(imageView)
        layout.addView(detailsText)
        layout.addView(removeButton)

        setContentView(layout)

        removeButton.setOnClickListener {
            val success = dbHelper.deleteAdvert(advertId)

            if (success) {
                Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error removing advert", Toast.LENGTH_SHORT).show()
            }
        }
    }
}