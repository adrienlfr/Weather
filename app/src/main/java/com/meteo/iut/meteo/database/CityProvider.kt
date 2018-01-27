package com.meteo.iut.meteo.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.meteo.iut.meteo.database.CityContract.CityEntry

/**
 * Created by adrien on 24/01/2018.
 */
class CityProvider : ContentProvider() {
    private lateinit var cityDbHelper : CityDbHelper

    private val CITIES = 1
    private val CITIES_ID = 2
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(CityContract.CONTENT_AUTHORITY, CityContract.PATH_CITIES, CITIES)
        uriMatcher.addURI(CityContract.CONTENT_AUTHORITY, CityContract.PATH_CITIES + "/#", CITIES_ID)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = uriMatcher.match(uri)

        val sqlDb = cityDbHelper.writableDatabase

        val id: Long
        when(uriType) {
            CITIES -> id = sqlDb.insert(CityEntry.CITY_TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }
        context.contentResolver.notifyChange(uri, null)
        return Uri.parse("${CityEntry.CITY_TABLE_NAME}/$id")
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = CityEntry.CITY_TABLE_NAME

        val uriType = uriMatcher.match(uri)

        when (uriType) {
            CITIES_ID -> queryBuilder.appendWhere("${CityEntry.CITY_KEY_ID}=${uri.lastPathSegment}")
            CITIES -> { }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        val cursor = queryBuilder.query(cityDbHelper.readableDatabase, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun onCreate(): Boolean {
        cityDbHelper = CityDbHelper(context)
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = uriMatcher.match(uri)
        val sqlDb = cityDbHelper.writableDatabase
        val rowsUpdated : Int

        when (uriType) {
            CITIES -> rowsUpdated = sqlDb.update(CityEntry.CITY_TABLE_NAME, values, selection, selectionArgs)
            CITIES_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDb.update(CityEntry.CITY_TABLE_NAME, values, "${CityEntry.CITY_KEY_ID}=${id}", null)
                } else {
                    rowsUpdated = sqlDb.update(CityEntry.CITY_TABLE_NAME, values, "${CityEntry.CITY_KEY_ID}=${id}AND${selection}", selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }

        context.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = uriMatcher.match(uri)
        val sqlDb = cityDbHelper.writableDatabase
        val rowsDeleted: Int

        when (uriType) {
            CITIES -> rowsDeleted = sqlDb.delete(CityEntry.CITY_TABLE_NAME, selection, selectionArgs)
            CITIES_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDb.delete(CityEntry.CITY_TABLE_NAME, "${CityEntry.CITY_KEY_ID}=${id}", null)
                } else {
                    rowsDeleted = sqlDb.delete(CityEntry.CITY_TABLE_NAME, "${CityEntry.CITY_KEY_ID}=${id}AND${selection}", selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: " + uri)
        }

        context.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}