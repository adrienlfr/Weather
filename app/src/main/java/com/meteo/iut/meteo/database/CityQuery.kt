package com.meteo.iut.meteo.database

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.meteo.iut.meteo.data.City

class CityQuery(context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver

    private val projection = arrayOf(
            CityContract.CityEntry.CITY_KEY_ID,
            CityContract.CityEntry.CITY_KEY_NAME,
            CityContract.CityEntry.CITY_KEY_DESCRIPTION,
            CityContract.CityEntry.CITY_KEY_TEMPERATURE,
            CityContract.CityEntry.CITY_KEY_HUMIDITY,
            CityContract.CityEntry.CITY_KEY_PRESSURE,
            CityContract.CityEntry.CITY_KEY_ICON_URL)

    fun addCity(cityName: String) : Uri{
        val uri: Uri
        val selection = "${CityContract.CityEntry.CITY_KEY_NAME} = \"$cityName\""
        val cityCursor = contentResolver.query(CityContract.CONTENT_URI, projection, selection, null, null)

        uri = if(cityCursor.moveToFirst()) {
            val cityValues = CityCursorWrapper(cityCursor).getCityContentValues()
            ContentUris.withAppendedId(CityContract.CONTENT_URI, cityValues.getAsLong(CityContract.CityEntry.CITY_KEY_ID))
        } else {
            val values = getCityContentValues(null, cityName, null, null, null, null, null)
            Uri.parse("${CityContract.BASE_CONTENT_URI}/${contentResolver.insert(CityContract.CONTENT_URI, values)}")
        }

        return uri
    }

    fun updateObservationCity(uriCity: Uri, description: String?, temperature: Float, humididy: String,
                              pressure: String, iconUrl: String): Int {

        val city = getCity(uriCity)
        var rowUpdate = 0
        city?.let {
            val values = getCityContentValues(city.id, city.name, description, temperature, humididy, pressure, iconUrl)
            rowUpdate = contentResolver.update(uriCity, values, null, null)
        }
        return rowUpdate
    }

    fun deleteCity(cityName: String): Boolean {
        var result = false

        val selection = "${CityContract.CityEntry.CITY_KEY_NAME} = \"$cityName\""

        val rowsDeleted = contentResolver.delete(CityContract.CONTENT_URI,
                selection, null)

        if (rowsDeleted > 0)
            result = true

        return result
    }

    fun getCity(uriCity: Uri): City? {

        val cursor = contentResolver.query(uriCity,
                projection, null, null, null)

        var city: City? = null

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0)).toLong()
            val cityName = cursor.getString(1)
            val cityDescription = cursor.getString(2)
            val cityTemperature = cursor.getString(3)?.toFloat()
            val cityHumidity = cursor.getString(4)
            val cityPressure = cursor.getString(5)
            val cityIconUrl = cursor.getString(6)

            city = City(id, cityName, cityDescription, cityTemperature, cityHumidity, cityPressure, cityIconUrl)
            cursor.close()
        }
        return city
    }

    private fun getCityContentValues(id: Long?, name: String, description: String?, temperature: Float?,
            humidity: String?, pressure: String?, iconUrl: String?) : ContentValues {

        val values = ContentValues()

        id?.let { values.put(CityContract.CityEntry.CITY_KEY_ID, id) }
        values.put(CityContract.CityEntry.CITY_KEY_NAME, name)
        description?.let { values.put(CityContract.CityEntry.CITY_KEY_DESCRIPTION, description) }
        temperature?.let { values.put(CityContract.CityEntry.CITY_KEY_TEMPERATURE, temperature) }
        humidity?.let { values.put(CityContract.CityEntry.CITY_KEY_HUMIDITY, humidity) }
        pressure?.let { values.put(CityContract.CityEntry.CITY_KEY_PRESSURE, pressure) }
        iconUrl?.let { values.put(CityContract.CityEntry.CITY_KEY_ICON_URL, iconUrl) }

        return values
    }
}