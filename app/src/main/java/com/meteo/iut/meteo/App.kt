package com.meteo.iut.meteo

import android.app.Application
import com.meteo.iut.meteo.meteo.MeteoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by adrien on 10/01/2018.
 */

private const val API_KEY = "7ebab482977ab59e"

class App : Application() {

    companion object {
        lateinit var instance: App

        val database: Database by lazy {
            Database(instance)
        }

        private val retrofit = Retrofit.Builder().baseUrl("http://api.wunderground.com/api/$API_KEY/geolookup/").addConverterFactory(GsonConverterFactory.create()).build()

        val meteoService: MeteoService = retrofit.create(MeteoService::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}