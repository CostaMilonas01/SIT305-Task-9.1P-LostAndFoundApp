package com.example.lostfoundapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.SimpleDateFormat
import java.util.*

class CreateAdvertActivity : AppCompatActivity() {

    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper

    private val imagePickCode = 1001
    private val autocompleteCode = 2001
    private val locationPermissionCode = 3001

    private lateinit var locationInput: EditText
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        initialisePlaces()

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 40, 40, 40)

        scrollView.addView(layout)

        val titleText = TextView(this)
        titleText.text = "Create Lost or Found Advert"
        titleText.textSize = 22f

        val typeGroup = RadioGroup(this)

        val lostRadio = RadioButton(this)
        lostRadio.text = "Lost"

        val foundRadio = RadioButton(this)
        foundRadio.text = "Found"

        typeGroup.addView(lostRadio)
        typeGroup.addView(foundRadio)

        val categorySpinner = Spinner(this)

        val categories = arrayOf(
            "Electronics",
            "Pets",
            "Wallets",
            "Keys",
            "Clothing",
            "Other"
        )

        categorySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        val nameInput = EditText(this)
        nameInput.hint = "Item name"

        val phoneInput = EditText(this)
        phoneInput.hint = "Phone number"

        val descriptionInput = EditText(this)
        descriptionInput.hint = "Description"

        val dateInput = EditText(this)
        dateInput.hint = "Date lost/found"

        locationInput = EditText(this)
        locationInput.hint = "Tap to search location"
        locationInput.isFocusable = false

        val currentLocationButton = Button(this)
        currentLocationButton.text = "Get Current Location"

        val imageButton = Button(this)
        imageButton.text = "Upload Image"

        selectedImageView = ImageView(this)
        selectedImageView.minimumHeight = 300

        val saveButton = Button(this)
        saveButton.text = "Save Advert"

        layout.addView(titleText)
        layout.addView(typeGroup)
        layout.addView(categorySpinner)
        layout.addView(nameInput)
        layout.addView(phoneInput)
        layout.addView(descriptionInput)
        layout.addView(dateInput)
        layout.addView(locationInput)
        layout.addView(currentLocationButton)
        layout.addView(imageButton)
        layout.addView(selectedImageView)
        layout.addView(saveButton)

        setContentView(scrollView)

        locationInput.setOnClickListener {
            openLocationAutocomplete()
        }

        currentLocationButton.setOnClickListener {
            getCurrentLocation()
        }

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

            // If the user picked/typed a place but coordinates are missing,
            // this tries to convert the address into latitude and longitude.
            if (selectedLatitude == null || selectedLongitude == null) {
                geocodeTypedLocation(locationInput.text.toString())
            }

            if (selectedLatitude == null || selectedLongitude == null) {
                Toast.makeText(this, "Please choose a valid location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadio = findViewById<RadioButton>(selectedTypeId)

            val timestamp = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

            val advert = Advert(
                id = 0,
                type = selectedRadio.text.toString(),
                category = categorySpinner.selectedItem.toString(),
                name = nameInput.text.toString(),
                phone = phoneInput.text.toString(),
                description = descriptionInput.text.toString(),
                date = dateInput.text.toString(),
                location = locationInput.text.toString(),
                latitude = selectedLatitude!!,
                longitude = selectedLongitude!!,
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

    private fun initialisePlaces() {
        if (!Places.isInitialized()) {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )

            val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY") ?: ""

            if (apiKey.isNotBlank() && apiKey != "YOUR_GOOGLE_MAPS_API_KEY") {
                Places.initialize(applicationContext, apiKey)
            }
        }
    }

    private fun openLocationAutocomplete() {
        if (!Places.isInitialized()) {
            Toast.makeText(
                this,
                "Add a Google Maps API key before using autocomplete",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        ).build(this)

        startActivityForResult(intent, autocompleteCode)
    }

    private fun getCurrentLocation() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )

            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Toast.makeText(
                    this,
                    "Could not get current location. Try again on the emulator/device.",
                    Toast.LENGTH_LONG
                ).show()

                return@addOnSuccessListener
            }

            selectedLatitude = location.latitude
            selectedLongitude = location.longitude

            val addressText = getAddressFromCoordinates(
                location.latitude,
                location.longitude
            )

            locationInput.setText(addressText)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())

            val addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )

            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "$latitude, $longitude"
            }
        } catch (e: Exception) {
            "$latitude, $longitude"
        }
    }

    @Suppress("DEPRECATION")
    private fun geocodeTypedLocation(locationText: String) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())

            val addresses = geocoder.getFromLocationName(
                locationText,
                1
            )

            if (!addresses.isNullOrEmpty()) {
                selectedLatitude = addresses[0].latitude
                selectedLongitude = addresses[0].longitude
            }
        } catch (e: Exception) {
            selectedLatitude = null
            selectedLongitude = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == locationPermissionCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else if (requestCode == locationPermissionCode) {
            Toast.makeText(
                this,
                "Location permission is needed for current location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == imagePickCode &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            selectedImageUri = data.data

            contentResolver.takePersistableUriPermission(
                selectedImageUri!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            selectedImageView.setImageURI(selectedImageUri)
        }

        if (
            requestCode == autocompleteCode &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            val place = Autocomplete.getPlaceFromIntent(data)

            locationInput.setText(
                place.address ?: place.name ?: "Selected location"
            )

            selectedLatitude = place.latLng?.latitude
            selectedLongitude = place.latLng?.longitude
        }
    }
}