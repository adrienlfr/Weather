package com.meteo.iut.meteo.activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.fragment.CityFragment
import com.meteo.iut.meteo.fragment.WeatherFragment

class CityActivity : AppCompatActivity(), CityFragment.CityFragmentListener {

    private lateinit var cityFragment: CityFragment
    private var weatherFragment: WeatherFragment? = null
    private var notificationManager: NotificationManager? = null

    var currentUriCity: Uri? = null
    var isTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        cityFragment = supportFragmentManager.findFragmentById(R.id.city_fragment) as CityFragment
        cityFragment.listener = this


        isTwoPane = findViewById<View>(R.id.weather_fragment) != null
        weatherFragment = supportFragmentManager.findFragmentById(R.id.weather_fragment) as WeatherFragment?

        if ( !isTwoPane ) {
            removeDisplayedFragment()
        }


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel("com.meteo.iut.meteo", "Notification Weather", "Exemple Weather")
    }


    override fun onCitySelected(uriCity: Uri, position: Int?) {
        currentUriCity = uriCity
        if (isTwoPane) {
            weatherFragment?.updateWeatherForCity(uriCity)
        } else {
            if (position != null) startWeatherActivity(position)
        }
    }

    override fun onClickNewNotification() {
        sendNotification()
    }

    override fun onEmptyCities() {
        weatherFragment?.initUi()
    }


    private fun startWeatherActivity(position: Int) {
        val activity = WeatherActivity().getIntent(this, position)
        startActivity(activity)
    }

    private fun removeDisplayedFragment() {
        if(weatherFragment != null) supportFragmentManager.beginTransaction().remove(weatherFragment).commit()
    }


    private fun sendNotification() {
        val notificationId = 101
        val weatherIntent = arrayOf(Intent(this, WeatherActivity::class.java))

        val pendingIntent = PendingIntent.getActivities(this, 0, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "com.meteo.iut.meteo"

        val icon = Icon.createWithResource(this, android.R.drawable.ic_dialog_info)

        val action: Notification.Action = Notification.Action.Builder(icon, "Open", pendingIntent).build()

        val notification = Notification.Builder(this@CityActivity, channelId)
                .setContentTitle("Titre")
                .setContentText("Voilà le texte!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setActions(action)
                .build()

        notificationManager?.notify(notificationId, notification)
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)
    }
}
