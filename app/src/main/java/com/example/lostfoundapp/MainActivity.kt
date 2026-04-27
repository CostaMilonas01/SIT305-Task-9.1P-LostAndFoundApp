package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val createButton = Button(this)
        createButton.text = "Create a New Advert"

        val showButton = Button(this)
        showButton.text = "Show Lost and Found Items"

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(40, 80, 40, 40)

        layout.addView(createButton)
        layout.addView(showButton)

        setContentView(layout)

        createButton.setOnClickListener {
            startActivity(Intent(this, CreateAdvertActivity::class.java))
        }

        showButton.setOnClickListener {
            startActivity(Intent(this, AdvertListActivity::class.java))
        }
    }
}