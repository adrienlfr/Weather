package com.meteo.iut.meteo.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.meteo.iut.meteo.database.CityContract.CityEntry

class CityDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "weather.db"

        private val CITY_TABLE_CREATE = """
            CREATE TABLE ${CityEntry.CITY_TABLE_NAME} (
                ${CityEntry.CITY_KEY_ID} INTEGER PRIMARY KEY,
                ${CityEntry.CITY_KEY_NAME} TEXT,
                ${CityEntry.CITY_KEY_DESCRIPTION} TEXT,
                ${CityEntry.CITY_KEY_TEMPERATURE} REAL,
                ${CityEntry.CITY_KEY_HUMIDITY} TEXT,
                ${CityEntry.CITY_KEY_PRESSURE} TEXT,
                ${CityEntry.CITY_KEY_ICON_URL} TEXT
                ${CityEntry.CITY_KEY_NAME} TEXT,
                ${CityEntry.CITY_ROW_INDEX} INTEGER
            )
            """

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${CityEntry.CITY_TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CITY_TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun countain(cityName: String): Cursor? {
        var cityCursor : Cursor? = null

        readableDatabase.rawQuery("SELECT * FROM ${CityEntry.CITY_TABLE_NAME} WHERE ${CityEntry.CITY_KEY_NAME} = $cityName", null).use { cursor ->
            if(cursor.moveToNext()) {
                cityCursor = cursor
            }
        }

        return cityCursor
    }
}
