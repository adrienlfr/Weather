package com.meteo.iut.meteo.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.fragment.CityFragment
import com.meteo.iut.meteo.fragment.WeatherFragment
import com.meteo.iut.meteo.utils.Extra

class CityActivity : AppCompatActivity(), CityFragment.CityFragmentListener {

    private lateinit var cityFragment: CityFragment
    private var weatherFragment: WeatherFragment? = null

    var currentUriCity: Uri? = null
    var isTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        cityFragment = supportFragmentManager.findFragmentById(R.id.city_fragment) as CityFragment
        cityFragment.listener = this


        isTwoPane = findViewById<View>(R.id.weather_fragment) != null
        weatherFragment = supportFragmentManager.findFragmentById(R.id.weather_fragment) as WeatherFragment?

        if ( !isTwoPane ) {
            removeDisplayedFragment()
        }
    }


    override fun onCitySelected(uriCity: Uri, position: Int?) {
        currentUriCity = uriCity
        if (isTwoPane) {
            weatherFragment?.updateWeatherForCity(uriCity)
        } else {
            startWeatherActivity(uriCity)
        }
    }

    private fun removeDisplayedFragment() {
        if(weatherFragment != null) supportFragmentManager.beginTransaction().remove(weatherFragment).commit()
    }

    override fun onEmptyCities() {
        weatherFragment?.initUi()
    }

    private fun startWeatherActivity(uriCity: Uri) {
        val intent = Intent( this, WeatherActivity::class.java)
        intent.putExtra(Extra.EXTRA_CITY_URI, uriCity)
        startActivity(intent)
    }
}
