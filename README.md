# Lost and Found Map Mobile App - Task 9.1P

## Overview

This is my Task 9.1P Lost and Found Map Mobile App for SIT305. This project builds on my previous Task 7.1P Lost and Found app by adding geo-location features.

The app allows users to create lost or found item adverts, save item details, attach an image, choose a location, and view saved items on a Google Map. The main update for Task 9.1P is that each advert now stores latitude and longitude, allowing items to be shown as markers on a map.

## Features

- Create a lost or found advert
- Add item category, name, phone number, description, date, and image
- Select a location using Google Places autocomplete
- Use the device current location
- Save advert details locally using SQLite
- View saved adverts in a list
- View advert details
- Remove saved adverts
- Show saved adverts on Google Maps
- Radius-based search to show only items within a selected distance from the user

## Geo Features Added for Task 9.1P

For Task 9.1P, I added Google Maps and location features to the app.

The user can select a location in two ways:

1. By tapping the location text box and using Google Places autocomplete.
2. By pressing the **Get Current Location** button to use the device/emulator location.

When an advert is saved, the app stores both the written address and the latitude and longitude values. These coordinates are then used to display the advert on the map.

The home screen also includes a radius input. When the user enters a radius, such as 10 km, and presses **Show On Map**, the app only shows adverts that are within that distance from the user’s current location.

## Technologies Used

- Kotlin
- Android Studio
- SQLite
- Google Maps SDK for Android
- Google Places API
- Google Play Services Location
- Android Emulator or Android Device

## Project Setup

To run this project, open it in Android Studio.

You will need a Google Maps API key for the map and autocomplete features.

### Required Google APIs

Enable these APIs in Google Cloud Console:

- Maps SDK for Android
- Places API

### Add API Key

In the project, open the `local.properties` file and add:

```properties
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
Replace YOUR_GOOGLE_MAPS_API_KEY with your real Google Maps API key.

How to Run
Open the project in Android Studio.
Wait for Gradle to sync.
Add your Google Maps API key to local.properties.
Run the app on an emulator or Android device.
Allow location permission when the app asks for it.
How to Test the App
Create an Advert Using Autocomplete
Open the app.
Press Create a New Advert.
Fill in the item details.
Tap the location text box.
Search for and select a location using autocomplete.
Upload an image.
Press Save Advert.
Create an Advert Using Current Location
Press Create a New Advert.
Fill in the item details.
Press Get Current Location.
Allow location permission.
Upload an image.
Press Save Advert.
Show Items on Map
Return to the home screen.
Enter a radius, for example 10.
Press Show On Map.
The app will show nearby saved adverts as markers on the map.
Radius Search Explanation

The radius search works by getting the user’s current location and comparing it with the latitude and longitude of each saved advert.

If the advert is within the selected radius, it is displayed on the map. If the advert is outside the radius, it is not shown.

The distance is calculated in metres and then converted into kilometres.

Main Code Files
MainActivity.kt

This file controls the home screen. It allows the user to create an advert, view the advert list, enter a radius, and open the map screen.

CreateAdvertActivity.kt

This file handles advert creation. It includes the item form, image upload, location autocomplete, current location button, and saving the advert to the database.

MapActivity.kt

This file displays Google Maps. It gets the user’s current location, loads saved adverts, filters them by radius, and displays the matching adverts as map markers.

DatabaseHelper.kt

This file manages the SQLite database. It stores advert details, including latitude and longitude.

Advert.kt

This is the data class for a lost or found advert. It stores the item details, image URI, location text, latitude, longitude, and timestamp.

Notes

This project was completed for Task 9.1P. I am not a SIT708 student, so the SIT708-only commercial app report and architecture diagram section was not included.

Author

Costa Milonas
