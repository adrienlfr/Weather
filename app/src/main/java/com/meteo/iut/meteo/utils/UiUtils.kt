package com.meteo.iut.meteo.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by adrien on 10/01/2018.
 */
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this,
            message,
            duration).show()
}