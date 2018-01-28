package com.meteo.iut.meteo.data

import com.google.gson.annotations.SerializedName

data class Weather(@SerializedName("current_observation") val currentObservation: CurrentObservation)