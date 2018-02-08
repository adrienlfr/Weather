package com.meteo.iut.meteo.adapter

import android.content.ContentUris
import android.content.ContentValues
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
        val values = CityCursorWrapper(cursor).getCityContentValues()
        val cityName = values.getAsString(CityEntry.CITY_KEY_NAME)
        database.updateCityIndex(cityName ,content);
    }
    fun moveItemFromTo(from: Int, to: Int){

        var compteur = from
        val values = ContentValues()

        while(compteur<=to){

            if ( compteur== from ) {
                values.put(CityEntry.CITY_ROW_INDEX, from)
            }else if (compteur == to){
                values.put(CityEntry.CITY_ROW_INDEX, to)

            }
            else {
                if (from < to) {
                    values.put(CityEntry.CITY_ROW_INDEX, +1)
                } else {
                    values.put(CityEntry.CITY_ROW_INDEX, -1)
                }
            }
            updateCity(cursor!!,values)
            cursor!!.moveToNext()
            compteur++
        }


        //(" Select " + CityContract.CityEntry.CITY_KEY_ID +" from "+ CityContract.CityEntry.CITY_TABLE_NAME+"where rows_index between"+from +" and "+to)
        //sqlDb.execSQL(" Update " + row +" from "+ CityContract.CityEntry.CITY_TABLE_NAME+"set rows_index ="+to)
        //sqlDb.execSQL(" Update " + row +" from "+ CityContract.CityEntry.CITY_TABLE_NAME+"set rows_index ="+from)
        //sqlDb.execSQL(" Update " + row +" from "+ CityContract.CityEntry.CITY_TABLE_NAME+"set rows_index+=1")
        //sqlDb.execSQL(" Update " + row +" from "+ CityContract.CityEntry.CITY_TABLE_NAME+"set rows_index-=1")

        /*var tab = getAllCityBetween(from,to)
        val firstCursor:Cursor
        val lastCursor:Cursor


        var compteur : Int=0
        tab.forEach { row ->
            val values = CityCursorWrapper(Cursor).getCityContentValues()
            val cityName = values.getAsString(CityEntry.CITY_KEY_NAME)

            if ( compteur==0 ) {
                .updateCursorRowIndex(endCursor)
            }else if (compteur == tab.size){
                row.updateCursorRowIndex(startCursor)
            }
            else {
                if (from < to) {
                    row.incrementCursorRowIndex()
                } else {
                    row.decrementCursorRowIndex()
                }
            }
            compteur++
        }*/

    }

}