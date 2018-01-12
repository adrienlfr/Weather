package com.meteo.iut.meteo.meteo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.meteo.iut.meteo.App
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.utils.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by adrien on 10/01/2018.
 */
class MeteoFragment : Fragment() {

    companion object {
        val EXTRA_CITY_NAME = "com.meteo.itu.meteo.extras.EXTRA_CITY_NAME"
        fun newInstance() = MeteoFragment()
    }

    private lateinit var cityName: String
    private lateinit var city: TextView
    private lateinit var meteoIcon: ImageView
    private lateinit var meteoDescription: TextView
    private lateinit var temperature: TextView
    private lateinit var humidity: TextView
    private lateinit var pressure: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_meteo, container, false)

        city = view.findViewById(R.id.city)
        meteoIcon = view.findViewById(R.id.meteo_icon)
        meteoDescription = view.findViewById(R.id.meteo_description)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.humidity)
        pressure = view.findViewById(R.id.pressure)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent!!.hasExtra(EXTRA_CITY_NAME)) {
            updateMeteoForCity(activity!!.intent.getStringExtra(EXTRA_CITY_NAME))
        }
    }

    private fun updateMeteoForCity(cityName: String) {
        this.cityName = cityName

        this.city.text = cityName

        val call = App.meteoService.getMeteo(cityName)
        call.enqueue(object: Callback<Meteo> {
            override fun onResponse(call: Call<Meteo>?, response: Response<Meteo>?) {
                response?.body()?.currentObservation?.let { updateUi(it) }
            }

            override fun onFailure(call: Call<Meteo>?, t: Throwable?) {
                context.toast(getString(R.string.failed_sync_data))
            }
        })
    }

    private fun updateUi(currentObservation: CurrentObservation) {
        meteoDescription.text = currentObservation.meteo
        temperature.text = getString(R.string.meteo_temperature_value, currentObservation.temperature.toInt())
        humidity.text = getString(R.string.meteo_humidity_value, currentObservation.humidity)
        pressure.text = getString(R.string.meteo_pressure_value, currentObservation.pressure)
    }
}