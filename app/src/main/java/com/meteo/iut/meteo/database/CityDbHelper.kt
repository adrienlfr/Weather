package com.meteo.iut.meteo.database

import android.content.Context
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
                ${CityEntry.CITY_KEY_NAME} TEXT
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

}
