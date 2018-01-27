package com.meteo.iut.meteo.database

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.meteo.iut.meteo.data.City
import com.meteo.iut.meteo.database.CityContract.CityEntry

/**
 * Created by adrien on 24/01/2018.
 */
class CityDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val contentResolver: ContentResolver = context.contentResolver

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "weather.db"

        private val CITY_TABLE_CREATE = """
            CREATE TABLE ${CityEntry.CITY_TABLE_NAME} (
                ${CityEntry.CITY_KEY_ID} INTEGER PRIMARY KEY,
                ${CityEntry.CITY_KEY_NAME} TEXT
            )
            """

        private val CITY_QUERY_SELECT_ALL = "SELECT * FROM ${CityEntry.CITY_TABLE_NAME}"
        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${CityEntry.CITY_TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CITY_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun addCity(city: City){
        val values = ContentValues()
        values.put(CityEntry.CITY_KEY_NAME, city.name)

        contentResolver.insert(CityContract.BASE_CONTENT_URI, values)
    }

   fun getCity(cityName: String): City? {
        val projection = arrayOf(CityEntry.CITY_KEY_ID, CityEntry.CITY_KEY_NAME)

        val selection = "${CityEntry.CITY_KEY_NAME} = \"${cityName}\""

        val cursor = contentResolver.query(CityContract.BASE_CONTENT_URI,
                projection, selection, null, null)

        var city: City? = null

        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0)).toLong()
            val cityName = cursor.getString(1)

            city = City(id,cityName)
            cursor.close()
        }
        return city
    }

    fun getAllCities(): MutableList<City> {
        val cities = mutableListOf<City>()

        readableDatabase.rawQuery(CITY_QUERY_SELECT_ALL, null).use { cursor ->
            while(cursor.moveToNext()) {
                val ville = City(cursor.getLong(cursor.getColumnIndex(CityEntry.CITY_KEY_ID)), cursor.getString(cursor.getColumnIndex(CityEntry.CITY_KEY_NAME)))
                cities.add(ville)
            }
        }

        return cities
    }

    fun deleteCity(cityName: String): Boolean {
        var result = false

        val selection = "${CityEntry.CITY_KEY_NAME} = \"${cityName}\""

        val rowsDeleted = contentResolver.delete(CityContract.BASE_CONTENT_URI,
                selection, null)

        if (rowsDeleted > 0)
            result = true

        return result
    }

}
