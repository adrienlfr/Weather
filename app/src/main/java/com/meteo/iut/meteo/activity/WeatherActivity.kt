package com.meteo.iut.meteo.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v4.view.ViewPager
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.adapter.PagerCursorAdapter
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.utils.Extra

class WeatherActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val WEATHER_LOADER = 0
    private lateinit var viewPager : ViewPager
    private lateinit var pagerAdapter : PagerCursorAdapter

    fun getIntent(context: Context, initialPosition: Int): Intent {
        val intent = Intent(context, WeatherActivity::class.java)
        intent.putExtra(Extra.EXTRA_INITIAL_POSITION, initialPosition)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        supportLoaderManager.initLoader(WEATHER_LOADER, null, this)

        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = PagerCursorAdapter(supportFragmentManager, null)
        viewPager.adapter = pagerAdapter
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
                CityEntry.CITY_KEY_ID,
                CityEntry.CITY_KEY_NAME
        )
        return CursorLoader(this, CityContract.CONTENT_URI, projection, null, null, null)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        pagerAdapter.swapCursor(null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        pagerAdapter.swapCursor(data)
        intent?.let { viewPager.currentItem = it.getIntExtra(Extra.EXTRA_INITIAL_POSITION, 0) }
    }
}