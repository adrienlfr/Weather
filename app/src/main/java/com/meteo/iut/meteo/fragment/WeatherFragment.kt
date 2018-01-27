package com.meteo.iut.meteo.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.meteo.iut.meteo.App
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.data.City
import com.meteo.iut.meteo.data.CurrentObservation
import com.meteo.iut.meteo.data.Weather
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityCursorWrapper
import com.meteo.iut.meteo.database.CityDbHelper
import com.meteo.iut.meteo.utils.toast
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by adrien on 10/01/2018.
 */
class WeatherFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val EXTRA_CITY_URI = "com.meteo.itu.meteo.extras.EXTRA_CITY_URI"
        fun newInstance() = WeatherFragment()
    }

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var uriCity: Uri
    private lateinit var database: CityDbHelper
    private lateinit var city: City
    private lateinit var cityName: TextView
    private lateinit var icon: ImageView
    private lateinit var description: TextView
    private lateinit var temperature: TextView
    private lateinit var humidity: TextView
    private lateinit var pressure: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = App.database
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        refreshLayout = view.findViewById(R.id.swipe_refresh)
        cityName = view.findViewById(R.id.city)
        icon = view.findViewById(R.id.weather_icon)
        description = view.findViewById(R.id.weather_description)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.humidity)
        pressure = view.findViewById(R.id.pressure)

        refreshLayout.setOnRefreshListener { updateWeatherForCity(city.name) }

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent!!.hasExtra(EXTRA_CITY_URI)) {
            updateWeatherForCity(activity!!.intent.getParcelableExtra<Uri>(EXTRA_CITY_URI))
        }
    }


    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        data?.let {
            if(data.count > 0) {
                data.moveToFirst()
                val values = CityCursorWrapper(data).getCityContentValues()

                updateWeatherForCity(values.getAsString(CityEntry.CITY_KEY_NAME))
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        initUi()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
                CityEntry.CITY_KEY_ID,
                CityEntry.CITY_KEY_NAME
        )

        return CursorLoader(context, CityContract.CONTENT_URI, projection, null, null, null)
    }


    fun updateWeatherForCity(uriCity: Uri){
        this.uriCity = uriCity
        city = database.getCity(uriCity) as City
        updateWeatherForCity(city.name)
    }

    fun updateWeatherForCity(cityName: String) {
        this.cityName.text = cityName

        if (!refreshLayout.isRefreshing){
            refreshLayout.isRefreshing = true
        }

        val call = App.WEATHER_SERVICE.getMeteo(cityName)
        call.enqueue(object: Callback<Weather> {
            override fun onResponse(call: Call<Weather>?, response: Response<Weather>?) {
                response?.body()?.currentObservation?.let { updateUi(it) }
                refreshLayout.isRefreshing = false
            }
            override fun onFailure(call: Call<Weather>?, t: Throwable?) {
                context.toast(getString(R.string.failed_sync_data))
                refreshLayout.isRefreshing = false
            }
        })
    }

    private fun updateUi(weatherCurrentObservation: CurrentObservation) {

        Picasso.with(context)
                .load(weatherCurrentObservation.iconUrl)
                .placeholder(R.drawable.ic_cloud_off_black_24dp)
                .into(icon)

        description.text = weatherCurrentObservation.weather
        temperature.text = getString(R.string.meteo_temperature_value, weatherCurrentObservation.temperature.toInt())
        humidity.text = getString(R.string.meteo_humidity_value, weatherCurrentObservation.humidity)
        pressure.text = getString(R.string.meteo_pressure_value, weatherCurrentObservation.pressure)
    }

    fun initUi() {
        icon.setImageResource(R.drawable.ic_cloud_off_black_24dp)
        cityName.text = ""
        description.text = ""
        temperature.text = ""
        humidity.text = ""
        pressure.text = ""
    }
}