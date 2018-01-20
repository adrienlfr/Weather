package com.meteo.iut.meteo.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.meteo.iut.meteo.fragment.WeatherFragment

/**
 * Created by adrien on 10/01/2018.
 */
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                                .replace(android.R.id.content, WeatherFragment.newInstance())
                                .commit()
    }
}