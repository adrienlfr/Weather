package com.meteo.iut.meteo.database

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by adrien on 24/01/2018.
 */
object CityContract {

    val CONTENT_AUTHORITY = "com.meteo.iut.meteo"
    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
    val PATH_CITIES = "cities"

    abstract class CityEntry : BaseColumns {
        companion object {
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CITIES)

            val CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITIES
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITIES


            val CITY_TABLE_NAME = "city"
            val CITY_KEY_ID = "id"
            val CITY_KEY_NAME = "name"
        }
    }
}