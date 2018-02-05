package com.meteo.iut.meteo.adapter

import android.content.ContentUris
import android.database.Cursor
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.fragment.WeatherFragment


class PagerCursorAdapter(fragmentManager: FragmentManager,
                         private var cursor: Cursor?) : FragmentStatePagerAdapter(fragmentManager) {
    private val NO_COLUMN_ID = -1
    private var rowIDColumn = NO_COLUMN_ID

    override fun getItem(position: Int): Fragment? {
        cursor?.let {
            if(it.moveToPosition(position))
                return WeatherFragment.newInstance(ContentUris.withAppendedId(CityContract.CONTENT_URI, it.getLong(rowIDColumn)))
        }
        return null
    }

    override fun getCount(): Int {
        return cursor?.count ?: 0
    }

    fun swapCursor(newCursor : Cursor?) : Cursor? {
        newCursor?.let {
            val oldCursor = cursor
            cursor = it
            rowIDColumn = it.getColumnIndexOrThrow(CityEntry.CITY_KEY_ID)
            notifyDataSetChanged()
            return oldCursor
        }
        return null
    }
}