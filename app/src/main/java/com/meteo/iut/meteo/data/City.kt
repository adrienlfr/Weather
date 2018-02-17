package com.meteo.iut.meteo.data

data class City(var id : Long, var name : String, var description: String?, var temperature: Float?,
                var humidity: String?, var pressure: String?, var iconUrl: String?)