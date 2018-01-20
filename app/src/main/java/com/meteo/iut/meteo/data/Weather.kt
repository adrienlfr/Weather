package com.meteo.iut.meteo.data

import com.google.gson.annotations.SerializedName

/**
 * Created by adrien on 11/01/2018.
 */
data class Weather(@SerializedName("current_observation") val currentObservation: CurrentObservation)