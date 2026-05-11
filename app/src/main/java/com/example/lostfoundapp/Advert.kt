package com.example.lostfoundapp

data class Advert(
    val id: Int,
    val type: String,
    val category: String,
    val name: String,
    val phone: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val imageUri: String,
    val timestamp: String
)