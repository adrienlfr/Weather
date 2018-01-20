package com.meteo.iut.meteo.utils

import com.meteo.iut.meteo.data.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by adrien on 11/01/2018.
 */
interface WeatherService {

    @GET("conditions/lang:FR/q/FR/{cityName}.json")
    fun getMeteo(@Path("cityName") cityName: String) : Call<Weather>
}