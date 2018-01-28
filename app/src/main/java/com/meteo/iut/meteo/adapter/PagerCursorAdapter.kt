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
        if (cursor != null && cursor!!.moveToPosition(position)) {
            return WeatherFragment.newInstance(ContentUris.withAppendedId(CityContract.CONTENT_URI, cursor!!.getLong(rowIDColumn)))
        }
        return null
    }

    override fun getCount(): Int {
        return if(cursor == null) 0 else cursor!!.count
    }

    fun swapCursor(newCursor : Cursor?) : Cursor? {
        if (newCursor == cursor)
            return null

        val oldCursor = cursor
        cursor = newCursor
        rowIDColumn = if(cursor == null) NO_COLUMN_ID else cursor!!.getColumnIndexOrThrow(CityEntry.CITY_KEY_ID)
        notifyDataSetChanged()
        return oldCursor
    }
}