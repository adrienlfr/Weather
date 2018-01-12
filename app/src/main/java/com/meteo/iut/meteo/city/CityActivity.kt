package com.meteo.iut.meteo.city

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.meteo.MeteoActivity
import com.meteo.iut.meteo.meteo.MeteoFragment

class CityActivity : AppCompatActivity(), CityFragment.CityFragmentListener {

    private lateinit var cityFragment: CityFragment
    private lateinit var currentCity: City

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        cityFragment = supportFragmentManager.findFragmentById(R.id.city_fragment) as CityFragment
        cityFragment.listener = this
    }

    override fun onCitySelected(city: City) {
        currentCity = city
        startMeteoActivity(city)
    }

    private fun startMeteoActivity(city: City) {
        val intent = Intent( this, MeteoActivity::class.java)
        intent.putExtra(MeteoFragment.EXTRA_CITY_NAME, city.name)
        startActivity(intent)
    }
}
