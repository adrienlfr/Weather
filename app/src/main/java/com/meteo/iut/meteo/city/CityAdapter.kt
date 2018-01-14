package com.meteo.iut.meteo.city

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.meteo.iut.meteo.R

/**
 * Created by adrien on 10/01/2018.
 */
class CityAdapter(
        private val villes: List<City>,
        private val cityListener: CityAdapter.CityItemListener) : RecyclerView.Adapter<CityAdapter.ViewHolder>(),
        View.OnClickListener {


    interface CityItemListener {
        fun onCitySelected(city: City)
        fun onCityDeleted(city: City)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<CardView>(R.id.card_view)!!
        val cityNameView = itemView.findViewById<TextView>(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent?.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ville = villes[position]
        with(holder) {
            cardView.tag = ville
            cardView.setOnClickListener(this@CityAdapter)
            cityNameView.text = ville.name
        }
    }

    override fun getItemCount(): Int = villes.size

    override fun onClick(view: View) {
        when(view.id) {
            R.id.card_view -> cityListener.onCitySelected(view.tag as City)
        }
    }

    fun getItem(position: Int) : City {
        return villes[position]
    }
}