package com.meteo.iut.meteo.adapter

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import com.meteo.iut.meteo.data.City
import com.meteo.iut.meteo.database.CityContract.CityEntry

abstract class RecyclerViewCursorAdapter<ViewHolder : RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {

    var cursor: Cursor? = null
    private var rowIDColumn = -1

    init {
        setHasStableIds(true)
    }

    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        cursor = newCursor
        rowIDColumn = newCursor?.getColumnIndexOrThrow(CityEntry.CITY_KEY_ID) ?: -1
        notifyDataSetChanged()
        return oldCursor
    }

    override fun getItemCount(): Int {
        return if (cursor == null) 0 else cursor!!.count
    }

    fun getItem(position: Int): Cursor? {
        if (cursor != null) {
            cursor!!.moveToPosition(position)
        }
        return cursor
    }

    override fun getItemId(position: Int): Long {
        return if (cursor != null && cursor!!.moveToPosition(position)) {
            cursor!!.getLong(rowIDColumn)
        } else RecyclerView.NO_ID
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor == null) {
            throw IllegalStateException("this should only be called when the cursor is not null")
        }
        if (!cursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position " + position)
        }
        onBindViewHolder(holder, cursor!!)
    }

    fun positionOfCity(city : City) : Int? {
        var position: Int? = null
        var find = false

        if (cursor != null && cursor!!.moveToFirst()) {
            do {
                if (position == null) position = 0 else position++

                val id = Integer.parseInt(cursor!!.getString(0)).toLong()
                if (id == city.id){
                    find = true
                }
            } while (cursor!!.moveToNext() && !find)
        }

        if (!find) position = null
        return position
    }

    abstract fun onBindViewHolder(holder: ViewHolder, cursor: Cursor)
}