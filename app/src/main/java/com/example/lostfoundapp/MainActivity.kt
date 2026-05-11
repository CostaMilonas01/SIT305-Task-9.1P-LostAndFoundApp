package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 80, 40, 40)

        val titleText = TextView(this)
        titleText.text = "Lost and Found App"
        titleText.textSize = 24f

        val createButton = Button(this)
        createButton.text = "Create a New Advert"

        val showListButton = Button(this)
        showListButton.text = "Show Lost and Found List"

        val radiusInput = EditText(this)
        radiusInput.hint = "Radius in km, e.g. 10"
        radiusInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        val showMapButton = Button(this)
        showMapButton.text = "Show On Map"

        layout.addView(titleText)
        layout.addView(createButton)
        layout.addView(showListButton)
        layout.addView(radiusInput)
        layout.addView(showMapButton)

        setContentView(layout)

        createButton.setOnClickListener {
            val intent = Intent(this, CreateAdvertActivity::class.java)
            startActivity(intent)
        }

        showListButton.setOnClickListener {
            val intent = Intent(this, AdvertListActivity::class.java)
            startActivity(intent)
        }

        showMapButton.setOnClickListener {
            val radiusText = radiusInput.text.toString()

            val radiusKm = if (radiusText.isBlank()) {
                10.0
            } else {
                radiusText.toDoubleOrNull()
            }

            if (radiusKm == null || radiusKm <= 0) {
                Toast.makeText(
                    this,
                    "Please enter a valid radius",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("radiusKm", radiusKm)

            startActivity(intent)
        }
    }
}