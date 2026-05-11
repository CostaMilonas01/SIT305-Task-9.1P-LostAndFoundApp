package com.example.lostfoundapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var dbHelper: DatabaseHelper
    private var googleMap: GoogleMap? = null
    private var radiusKm: Double = 10.0
    private val locationPermissionCode = 4001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        radiusKm = intent.getDoubleExtra("radiusKm", 10.0)

        val mapFragment = SupportMapFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        showItemsNearCurrentLocation()
    }

    private fun showItemsNearCurrentLocation() {
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

        googleMap?.isMyLocationEnabled = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { userLocation ->
            if (userLocation == null) {
                Toast.makeText(
                    this,
                    "Could not get current location. Showing all saved items instead.",
                    Toast.LENGTH_LONG
                ).show()

                showAllItemsWithoutRadius()

                return@addOnSuccessListener
            }

            googleMap?.clear()

            val userLatLng = LatLng(
                userLocation.latitude,
                userLocation.longitude
            )

            googleMap?.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("Your current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )

            val nearbyItems = getItemsInsideRadius(
                userLocation,
                radiusKm
            )

            addAdvertMarkers(nearbyItems)

            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(userLatLng, 13f)
            )

            Toast.makeText(
                this,
                "Showing ${nearbyItems.size} item(s) within $radiusKm km",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getItemsInsideRadius(
        userLocation: Location,
        radiusKm: Double
    ): List<Advert> {
        val allItems = dbHelper.getAllAdverts()
        val results = ArrayList<Advert>()

        for (advert in allItems) {
            val itemLocation = Location("advert")

            itemLocation.latitude = advert.latitude
            itemLocation.longitude = advert.longitude

            val distanceKm = userLocation.distanceTo(itemLocation) / 1000.0

            if (distanceKm <= radiusKm) {
                results.add(advert)
            }
        }

        return results
    }

    private fun showAllItemsWithoutRadius() {
        val allItems = dbHelper.getAllAdverts()

        googleMap?.clear()

        addAdvertMarkers(allItems)

        if (allItems.isNotEmpty()) {
            val firstItem = LatLng(
                allItems[0].latitude,
                allItems[0].longitude
            )

            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(firstItem, 11f)
            )
        }
    }

    private fun addAdvertMarkers(adverts: List<Advert>) {
        for (advert in adverts) {
            val position = LatLng(
                advert.latitude,
                advert.longitude
            )

            googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("${advert.type}: ${advert.name}")
                    .snippet("${advert.category} - ${advert.location}")
            )
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
            showItemsNearCurrentLocation()
        } else if (requestCode == locationPermissionCode) {
            Toast.makeText(
                this,
                "Location permission is needed for radius search",
                Toast.LENGTH_LONG
            ).show()

            showAllItemsWithoutRadius()
        }
    }
}