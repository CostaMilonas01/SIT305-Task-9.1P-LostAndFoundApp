package com.example.lostfoundapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "lost_found.db", null, 1) {

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
                imageUri TEXT NOT NULL,
                timestamp TEXT NOT NULL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS adverts")
        onCreate(db)
    }

    fun insertAdvert(advert: Advert): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put("type", advert.type)
        values.put("category", advert.category)
        values.put("name", advert.name)
        values.put("phone", advert.phone)
        values.put("description", advert.description)
        values.put("date", advert.date)
        values.put("location", advert.location)
        values.put("imageUri", advert.imageUri)
        values.put("timestamp", advert.timestamp)

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
                advertList.add(
                    Advert(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                    )
                )
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
                advertList.add(
                    Advert(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return advertList
    }

    fun getAdvertById(id: Int): Advert? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM adverts WHERE id = ?", arrayOf(id.toString()))

        var advert: Advert? = null

        if (cursor.moveToFirst()) {
            advert = Advert(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
            )
        }

        cursor.close()
        db.close()
        return advert
    }

    fun deleteAdvert(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("adverts", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}