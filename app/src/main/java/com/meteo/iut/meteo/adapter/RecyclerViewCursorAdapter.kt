package com.meteo.iut.meteo.adapter

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import com.meteo.iut.meteo.database.CityContract.CityEntry

/**
 * Created by adrien on 27/01/2018.
 */
abstract class RecyclerViewCursorAdapter<ViewHolder : RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {

    var cursor: Cursor? = null
        private set
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

    abstract fun onBindViewHolder(holder: ViewHolder, cursor: Cursor)
}