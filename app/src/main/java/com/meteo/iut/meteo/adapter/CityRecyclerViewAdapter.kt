package com.meteo.iut.meteo.adapter

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import android.database.CursorWrapper
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.database.CityCursorWrapper
import com.meteo.iut.meteo.database.CityQuery
import com.meteo.iut.meteo.App

import java.net.URI


class CityRecyclerViewAdapter(

        private val cityListener: CityItemListener) : RecyclerViewCursorAdapter<CityRecyclerViewAdapter.ViewHolder>() {
    private lateinit var database : CityQuery

    interface CityItemListener {
        fun onCitySelected(uriCity: Uri, position: Int?)
        fun onCityDeleted(cursor: Cursor)
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityNameView = itemView.findViewById<TextView>(R.id.name)!!
        lateinit var cityUri: Uri
        init {
            itemView.setOnClickListener {
                cityListener.onCitySelected(cityUri, adapterPosition)
            }
        }
    }
    private fun updateCity(cursor: Cursor, content: ContentValues){
        database = App.database
        var values = CityCursorWrapper(cursor).getCityContentValues()
        var cityName = values.getAsString(CityEntry.CITY_KEY_NAME)
        database.updateCityIndex(cityName ,content)
    }

    fun moveItemFromTo(from: Int, to: Int){

        val values = ContentValues()
        values.put(CityEntry.CITY_ROW_INDEX, to)
        cursor!!.moveToPosition(from)
        updateCity(cursor!!, values)

        val values2 = ContentValues()
        values2.put(CityEntry.CITY_ROW_INDEX, from)
        cursor!!.moveToPosition(to)
        updateCity(cursor!!, values2)

        notifyDataSetChanged()

    }

}