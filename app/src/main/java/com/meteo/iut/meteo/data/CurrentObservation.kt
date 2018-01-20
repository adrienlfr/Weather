package com.meteo.iut.meteo.data

import com.google.gson.annotations.SerializedName

/**
 * Created by adrien on 11/01/2018.
 */
data class CurrentObservation(
        @SerializedName("weather") val meteo: String,
        @SerializedName("temp_c") val temperature: Float,
        @SerializedName("relative_humidity") val humidity: String,
        @SerializedName("pressure_mb") val pressure: String,
        @SerializedName("icon_url") val iconUrl: String
)