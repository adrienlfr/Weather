package com.meteo.iut.meteo.database

import android.content.ContentValues
import android.database.Cursor
import android.database.CursorWrapper
import com.meteo.iut.meteo.database.CityContract.CityEntry

class CityCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getCityContentValues() : ContentValues {
        val values = ContentValues()

        var id = getColumnIndex(CityEntry.CITY_KEY_ID)
        if(id != -1) values.put(CityEntry.CITY_KEY_ID, getLong(id))

        id = getColumnIndex(CityEntry.CITY_KEY_NAME)
        if(id != -1) values.put(CityEntry.CITY_KEY_NAME, getString(id))

        id = getColumnIndex(CityEntry.CITY_KEY_DESCRIPTION)
        if(id != -1) values.put(CityEntry.CITY_KEY_DESCRIPTION, getString(id))

        id = getColumnIndex(CityEntry.CITY_KEY_TEMPERATURE)
        if(id != -1) values.put(CityEntry.CITY_KEY_TEMPERATURE, getString(id))

        id = getColumnIndex(CityEntry.CITY_KEY_HUMIDITY)
        if(id != -1) values.put(CityEntry.CITY_KEY_HUMIDITY, getString(id))

        id = getColumnIndex(CityEntry.CITY_KEY_PRESSURE)
        if(id != -1) values.put(CityEntry.CITY_KEY_PRESSURE, getString(id))

        id = getColumnIndex(CityEntry.CITY_KEY_ICON_URL)
        if(id != -1) values.put(CityEntry.CITY_KEY_ICON_URL, getString(id))

        return values
    }
}