package com.example.lostfoundapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "lost_found.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE adverts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                category TEXT NOT NULL,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                description TEXT NOT NULL,
                date TEXT NOT NULL,
                location TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                imageUri TEXT NOT NULL,
                timestamp TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS adverts")
        onCreate(db)
    }

    fun insertAdvert(advert: Advert): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("type", advert.type)
            put("category", advert.category)
            put("name", advert.name)
            put("phone", advert.phone)
            put("description", advert.description)
            put("date", advert.date)
            put("location", advert.location)
            put("latitude", advert.latitude)
            put("longitude", advert.longitude)
            put("imageUri", advert.imageUri)
            put("timestamp", advert.timestamp)
        }

        val result = db.insert("adverts", null, values)
        db.close()

        return result != -1L
    }

    fun getAllAdverts(): ArrayList<Advert> {
        val advertList = ArrayList<Advert>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM adverts ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                advertList.add(cursorToAdvert(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return advertList
    }

    fun getAdvertsByCategory(category: String): ArrayList<Advert> {
        val advertList = ArrayList<Advert>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM adverts WHERE category = ? ORDER BY id DESC",
            arrayOf(category)
        )

        if (cursor.moveToFirst()) {
            do {
                advertList.add(cursorToAdvert(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return advertList
    }

    fun getAdvertById(id: Int): Advert? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM adverts WHERE id = ?",
            arrayOf(id.toString())
        )

        var advert: Advert? = null

        if (cursor.moveToFirst()) {
            advert = cursorToAdvert(cursor)
        }

        cursor.close()
        db.close()

        return advert
    }

    fun deleteAdvert(id: Int): Boolean {
        val db = writableDatabase

        val result = db.delete(
            "adverts",
            "id = ?",
            arrayOf(id.toString())
        )

        db.close()

        return result > 0
    }

    private fun cursorToAdvert(cursor: android.database.Cursor): Advert {
        return Advert(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
            category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
            location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
            latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
            longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")),
            imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
        )
    }
}