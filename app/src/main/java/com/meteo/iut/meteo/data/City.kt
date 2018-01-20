package com.meteo.iut.meteo.data

/**
 * Created by adrien on 10/01/2018.
 */
data class City(var id : Long, var name : String) {
    constructor(name: String) : this(-1, name)
}