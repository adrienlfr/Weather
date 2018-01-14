package com.meteo.iut.meteo.meteo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by adrien on 10/01/2018.
 */
class MeteoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                                .replace(android.R.id.content, MeteoFragment.newInstance())
                                .commit()
    }
}