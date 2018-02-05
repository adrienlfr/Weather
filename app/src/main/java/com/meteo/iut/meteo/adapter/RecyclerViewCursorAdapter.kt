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
        return cursor?.count ?: 0
    }

    fun getItem(position: Int): Cursor? {
        cursor?.run { moveToPosition(position) }
        return cursor
    }

    override fun getItemId(position: Int): Long {
        cursor?.let {
            if(it.moveToPosition(position))
                return it.getLong(rowIDColumn)
        }
        return RecyclerView.NO_ID
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.let {
            if (it.moveToPosition(position)) {
                onBindViewHolder(holder, it)
            } else {
                throw IllegalStateException("Couldn't move cursor to position " + position)
            }
        }
        if (cursor == null) {
            throw IllegalStateException("This should only be called when the cursor is not null")
        }
    }

    fun positionOfCity(city : City) : Int? {
        var find = false
        var position: Int? = null
        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    position = position?.inc() ?: 0
                    val id = Integer.parseInt(it.getString(0)).toLong()
                    if (id == city.id){
                        find = true
                    }
                } while (it.moveToNext() && !find)
            }
        }

        return if (find) position else null
    }

    abstract fun onBindViewHolder(holder: ViewHolder, cursor: Cursor)
}