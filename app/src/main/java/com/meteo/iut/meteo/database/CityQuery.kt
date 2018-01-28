package com.meteo.iut.meteo.database

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.meteo.iut.meteo.data.City

class CityQuery(context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun addCity(cityName: String) : Uri{
        val values = ContentValues()
        values.put(CityContract.CityEntry.CITY_KEY_NAME, cityName)

        return contentResolver.insert(CityContract.CONTENT_URI, values)
    }

    fun deleteCity(cityName: String): Boolean {
        var result = false

        val selection = "${CityContract.CityEntry.CITY_KEY_NAME} = \"${cityName}\""

        val rowsDeleted = contentResolver.delete(CityContract.CONTENT_URI,
                selection, null)

        if (rowsDeleted > 0)
            result = true

        return result
    }

    fun getCity(uriCity: Uri): City? {
        val projection = arrayOf(CityContract.CityEntry.CITY_KEY_ID, CityContract.CityEntry.CITY_KEY_NAME)


        val cursor = contentResolver.query(uriCity,
                projection, null, null, null)

        var city: City? = null

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0)).toLong()
            val cityName = cursor.getString(1)

            city = City(id,cityName)
            cursor.close()
        }
        return city
    }
}