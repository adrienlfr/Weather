package com.meteo.iut.meteo.database

import android.net.Uri
import android.provider.BaseColumns

object CityContract {

    val CONTENT_AUTHORITY = "com.meteo.iut.meteo.database.CityProvider"
    val PATH_CITIES = "cities"
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")!!
    val CONTENT_URI : Uri = Uri.parse("$BASE_CONTENT_URI/$PATH_CITIES")

    abstract class CityEntry : BaseColumns {
        companion object {
            val CITY_TABLE_NAME = PATH_CITIES
            val CITY_KEY_ID = BaseColumns._ID
            val CITY_KEY_NAME = "name"
            val CITY_KEY_DESCRIPTION = "description"
            val CITY_KEY_TEMPERATURE = "temperature"
            val CITY_KEY_HUMIDITY = "humidity"
            val CITY_KEY_PRESSURE = "pressure"
            val CITY_KEY_ICON_URL = "iconUrl"
        }
    }
}