package com.meteo.iut.meteo.meteo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by adrien on 11/01/2018.
 */
interface MeteoService {

    @GET("conditions/q/FR/{cityName}.json")
    fun getMeteo(@Path("cityName") cityName: String) : Call<Meteo>
}