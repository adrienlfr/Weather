package com.meteo.iut.meteo.meteo

import com.google.gson.annotations.SerializedName

/**
 * Created by adrien on 11/01/2018.
 */
data class Meteo(@SerializedName("current_observation") val currentObservation: CurrentObservation)