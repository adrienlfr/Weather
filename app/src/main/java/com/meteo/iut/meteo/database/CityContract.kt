package com.meteo.iut.meteo.database

import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by adrien on 24/01/2018.
 */
object CityContract {

    val CONTENT_AUTHORITY = "com.meteo.iut.meteo.database.CityProvider"
    val PATH_CITIES = "cities"
    val CONTENT_URI : Uri = Uri.parse("content://${CONTENT_AUTHORITY}/${PATH_CITIES}")

    abstract class CityEntry : BaseColumns {
        companion object {
            val CITY_TABLE_NAME = PATH_CITIES
            val CITY_KEY_ID = BaseColumns._ID
            val CITY_KEY_NAME = "name"
        }
    }
}