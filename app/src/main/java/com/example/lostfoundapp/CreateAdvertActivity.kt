package com.example.lostfoundapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateAdvertActivity : AppCompatActivity() {

    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper

    private val imagePickCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 40, 40, 40)

        val typeGroup = RadioGroup(this)
        val lostRadio = RadioButton(this)
        lostRadio.text = "Lost"
        val foundRadio = RadioButton(this)
        foundRadio.text = "Found"
        typeGroup.addView(lostRadio)
        typeGroup.addView(foundRadio)

        val categorySpinner = Spinner(this)
        val categories = arrayOf("Electronics", "Pets", "Wallets", "Keys", "Clothing", "Other")
        categorySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        val nameInput = EditText(this)
        nameInput.hint = "Name"

        val phoneInput = EditText(this)
        phoneInput.hint = "Phone"

        val descriptionInput = EditText(this)
        descriptionInput.hint = "Description"

        val dateInput = EditText(this)
        dateInput.hint = "Date"

        val locationInput = EditText(this)
        locationInput.hint = "Location"

        val imageButton = Button(this)
        imageButton.text = "Upload Image"

        selectedImageView = ImageView(this)
        selectedImageView.minimumHeight = 300

        val saveButton = Button(this)
        saveButton.text = "Save"

        layout.addView(typeGroup)
        layout.addView(categorySpinner)
        layout.addView(nameInput)
        layout.addView(phoneInput)
        layout.addView(descriptionInput)
        layout.addView(dateInput)
        layout.addView(locationInput)
        layout.addView(imageButton)
        layout.addView(selectedImageView)
        layout.addView(saveButton)

        setContentView(layout)


        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, imagePickCode)
        }

        saveButton.setOnClickListener {

            val selectedTypeId = typeGroup.checkedRadioButtonId

            if (selectedTypeId == -1) {
                Toast.makeText(this, "Please select Lost or Found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (
                nameInput.text.isEmpty() ||
                phoneInput.text.isEmpty() ||
                descriptionInput.text.isEmpty() ||
                dateInput.text.isEmpty() ||
                locationInput.text.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadio = findViewById<RadioButton>(selectedTypeId)

            val timestamp =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val advert = Advert(
                id = 0,
                type = selectedRadio.text.toString(),
                category = categorySpinner.selectedItem.toString(),
                name = nameInput.text.toString(),
                phone = phoneInput.text.toString(),
                description = descriptionInput.text.toString(),
                date = dateInput.text.toString(),
                location = locationInput.text.toString(),
                imageUri = selectedImageUri.toString(),
                timestamp = timestamp
            )

            val success = dbHelper.insertAdvert(advert)

            if (success) {
                Toast.makeText(this, "Advert saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePickCode && resultCode == Activity.RESULT_OK && data != null) {

            selectedImageUri = data.data


            contentResolver.takePersistableUriPermission(
                selectedImageUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            selectedImageView.setImageURI(selectedImageUri)
        }
    }
}