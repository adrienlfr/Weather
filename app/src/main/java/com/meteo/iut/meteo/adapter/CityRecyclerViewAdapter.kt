package com.meteo.iut.meteo.adapter

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.database.CityCursorWrapper


class CityRecyclerViewAdapter(
        private val cityListener: CityItemListener) : RecyclerViewCursorAdapter<CityRecyclerViewAdapter.ViewHolder>() {

    interface CityItemListener {
        fun onCitySelected(uriCity: Uri, position: Int?)
        fun onCityDeleted(cursor: Cursor)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityNameView = itemView.findViewById<TextView>(R.id.name)!!
        lateinit var cityUri: Uri
        init {
            itemView.setOnClickListener {
                    cityListener.onCitySelected(cityUri, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent?.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, cursor: Cursor) {
        val cityValues = CityCursorWrapper(cursor).getCityContentValues()

        with(holder) {
            cityUri = ContentUris.withAppendedId(CityContract.CONTENT_URI, cityValues.getAsLong(CityEntry.CITY_KEY_ID))
            cityNameView.text = cityValues.getAsString(CityEntry.CITY_KEY_NAME)
        }
    }
}