package com.meteo.iut.meteo.database

import android.app.PendingIntent.getActivity
import android.content.*
import android.database.Cursor
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.meteo.iut.meteo.data.City
import android.database.sqlite.SQLiteDatabase





class CityQuery(context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver

    private val projection = arrayOf(
            CityContract.CityEntry.CITY_KEY_ID,
            CityContract.CityEntry.CITY_KEY_NAME,
            CityContract.CityEntry.CITY_KEY_DESCRIPTION,
            CityContract.CityEntry.CITY_KEY_TEMPERATURE,
            CityContract.CityEntry.CITY_KEY_HUMIDITY,
            CityContract.CityEntry.CITY_KEY_PRESSURE,
            CityContract.CityEntry.CITY_KEY_ICON_URL,
            CityContract.CityEntry.CITY_ROW_INDEX)

    fun addCity(cityName: String) : Uri{
        val uri: Uri
        val selection = "${CityContract.CityEntry.CITY_KEY_NAME} = \"$cityName\""
        val cityCursor = contentResolver.query(CityContract.CONTENT_URI, projection, selection, null, null)
        val citiesCursor = contentResolver.query(CityContract.CONTENT_URI, projection, null, null, null)


        uri = if(cityCursor.moveToFirst()) {
            val cityValues = CityCursorWrapper(cityCursor).getCityContentValues()
            ContentUris.withAppendedId(CityContract.CONTENT_URI, cityValues.getAsLong(CityContract.CityEntry.CITY_KEY_ID))
        } else {
            val values = ContentValues()
            values.put(CityContract.CityEntry.CITY_KEY_NAME, cityName)
            values.put(CityContract.CityEntry.CITY_ROW_INDEX, (citiesCursor.count))
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




    fun updateCityIndex(cityName:String, content:ContentValues): Boolean{
        var result = false

        val selection = "${CityContract.CityEntry.CITY_KEY_NAME} = \"$cityName\""

        val rowsUpdated = contentResolver.update(CityContract.CONTENT_URI,content,
                selection, null)

        if (rowsUpdated > 0)
            result = true

        return result
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
    fun getCityPosition(uriCity: Uri): Int? {
        val cursor = contentResolver.query(uriCity, null, null, null, null)
        cursor.moveToPrevious()
        val position = cursor?.position
        cursor.close()
        return position
    }
}