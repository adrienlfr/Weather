package com.meteo.iut.meteo.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.meteo.iut.meteo.database.CityQuery
import com.meteo.iut.meteo.utils.Extra
import com.meteo.iut.meteo.utils.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_weather.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.CONNECTIVITY_SERVICE
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.R.id.message
import android.widget.Toast


class WeatherFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        fun newInstance(uriCity : Uri) : WeatherFragment {
            val args = Bundle()
            args.putParcelable(Extra.EXTRA_CITY_URI, uriCity)
            val fragment = WeatherFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var emptyViewWeather: View
    private lateinit var pressure_label: View
    private lateinit var humidity_label: View
    private lateinit var temperature_label: View
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var uriCity: Uri
    private lateinit var database: CityQuery
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
        if (activity?.intent!!.hasExtra(Extra.EXTRA_CITY_URI)) {
            arguments.putParcelable(Extra.EXTRA_CITY_URI, activity!!.intent.getParcelableExtra<Uri>(Extra.EXTRA_CITY_URI))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        emptyViewWeather =  view.findViewById(R.id.emptyViewWeather)
        temperature_label = view.findViewById(R.id.temperature_label)
        humidity_label = view.findViewById(R.id.humidity_label)
        temperature_label = view.findViewById(R.id.temperature_label)

        emptyViewWeather!!.setVisibility(View.VISIBLE)
        temperature_label!!.setVisibility(View.GONE)
        humidity_label!!.setVisibility(View.GONE)
        temperature_label!!.setVisibility(View.GONE)

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

        arguments?.let {
            if (!arguments.isEmpty) {
                updateWeatherForCity(arguments.getParcelable<Uri>(Extra.EXTRA_CITY_URI))
            }
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
        checkNetwork()
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
        val cityTemp = database.getCity(uriCity)
        cityTemp.let {
            city = cityTemp!!
            updateWeatherForCity(city!!.name)
        }
    }


    private fun checkIfCitySelected(name: String){
        if(name!=""){

            emptyViewWeather!!.setVisibility(View.GONE)

            temperature_label!!.setVisibility(View.VISIBLE)

            humidity_label!!.setVisibility(View.VISIBLE)

            temperature_label!!.setVisibility(View.VISIBLE)
        }
    }

    /*fun printNetWorkStatus(){
        if (!checkNetwork()){
            "Network is not connected, you will have the latest value of weather from your last connection".toast(context)
        }
    }*/

    fun Any.toast(context: Context) {
        Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
    }

    private fun updateWeatherForCity(cityName: String) {

        checkIfCitySelected(cityName)
        //printNetWorkStatus()
        checkNetwork()

        this.cityName.text = cityName
        if (!refreshLayout.isRefreshing){
            refreshLayout.isRefreshing = true
        }

        val call = App.WEATHER_SERVICE.getWeather(cityName)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun checkNetwork(){
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var info = connMgr.activeNetworkInfo
        if (info != null && info.isConnected()){
            "Network is not connected, you will have the latest value of weather from your last connection".toast(context)
        }
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