package com.meteo.iut.meteo.fragment

import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*

import com.meteo.iut.meteo.App
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.activity.CityActivity
import com.meteo.iut.meteo.adapter.CityRecyclerViewAdapter
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.database.CityCursorWrapper
import com.meteo.iut.meteo.database.CityQuery
import com.meteo.iut.meteo.dialog.CreateCityDialogFragment
import com.meteo.iut.meteo.dialog.DeleteCityDialogFragment
import com.meteo.iut.meteo.utils.DragManageAdapter
import com.meteo.iut.meteo.utils.SwipeToDeleteCallback
import com.meteo.iut.meteo.utils.toast


class CityFragment : Fragment(), CityRecyclerViewAdapter.CityItemListener, LoaderManager.LoaderCallbacks<Cursor> {

    interface CityFragmentListener {
        fun onCitySelected(uriCity: Uri, position: Int?)
        fun onEmptyCities()
        fun onClickNewNotification()
    }

    var listener: CityFragmentListener? = null
    private val CITY_LOADER = 0
    private var displayCity = false
    private var lastCityUriAdd: Uri? = null

    private lateinit var database : CityQuery
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var recyclerViewAdapter: CityRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = App.database
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_city, container, false)
        recyclerView = view.findViewById(R.id.cities_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val glm = GridLayoutManager(recyclerView.context, 1)
        recyclerView.layoutManager = glm

        recyclerViewAdapter = CityRecyclerViewAdapter(this)
        recyclerView.adapter = recyclerViewAdapter

        val swipeHandler = object : SwipeToDeleteCallback(this.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val cursor = recyclerViewAdapter.getItem(viewHolder.adapterPosition)
                cursor.let {showDeleteCityDialog(cursor!!)}
            }
        }

        val touchHelper = ItemTouchHelper(swipeHandler)
        touchHelper.attachToRecyclerView(recyclerView)

        floatingButton = view.findViewById(R.id.floatingActionButton)
        floatingButton.setOnClickListener({_ -> showCreateCityDialog()})
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loaderManager.initLoader(CITY_LOADER, null, this)

        val dragAndDropHandler = DragManageAdapter(recyclerViewAdapter, this.context,
                ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))

        val touchHelperDrag = ItemTouchHelper(dragAndDropHandler)
        touchHelperDrag.attachToRecyclerView(recyclerView)

    }


    override fun onLoaderReset(loader: Loader<Cursor>?) {
        recyclerViewAdapter.swapCursor(null)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
                CityEntry.CITY_KEY_ID,
                CityEntry.CITY_KEY_NAME
        )

        return CursorLoader(context, CityContract.CONTENT_URI, projection, null, null, null)
    }


    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        recyclerViewAdapter.swapCursor(data)
        if (displayCity){
            displayCurrentCity()
            displayCity = false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_city, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_create_city -> {
                showCreateCityDialog()
                return true
            }
            R.id.action_new_notification -> {
                listener?.onClickNewNotification()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCitySelected(uriCity: Uri, position: Int?) {
        listener?.onCitySelected(uriCity, position)
    }

    override fun onCityDeleted(cursor: Cursor) {
        showDeleteCityDialog(cursor)
    }

    private fun selectFirstCity(){
        when(recyclerViewAdapter.itemCount > 0) {
            true -> onCitySelected(recyclerViewAdapter.getItem(0)!!.notificationUri, null)
            false -> listener?.onEmptyCities()
        }
    }


    private fun showCreateCityDialog() {
        val createCityFragment = CreateCityDialogFragment()
        createCityFragment.listener = object : CreateCityDialogFragment.CreateCityDialogListerner {
            override fun onDialogPositiveClick(cityName: String) {
                saveCity(cityName)
            }

            override fun onDialogNegativeClick() { }
        }

        createCityFragment.show(fragmentManager, "CreateCityDialogFragment")
    }

    private fun showDeleteCityDialog(cursor: Cursor) {
        val values = CityCursorWrapper(cursor).getCityContentValues()
        val cityName = values.getAsString(CityEntry.CITY_KEY_NAME)

        val deleteCityFragment = DeleteCityDialogFragment.newInstance(cityName)
        deleteCityFragment.listener = object: DeleteCityDialogFragment.DeleteCityDialogListener {
            override fun onDialogPositiveClick() {
                deleteCity(cityName)
            }

            override fun onDialogNegativeClick() {
            }
        }
        deleteCityFragment.show(fragmentManager, "DeleteCityDialogFragment")
    }

    private fun deleteCity(cityName: String) {
        if ( database.deleteCity(cityName) ) {
            context.toast(getString(R.string.deletecity_found, cityName))
            if ((activity as CityActivity).isTwoPane && CityActivity.currentUriCity != null) {
                if (database.getCity(CityActivity.currentUriCity!!) == null)
                    selectFirstCity()
            }
        }else{
            context.toast(getString(R.string.deletecity_impossible, cityName))
        }
    }


    private fun saveCity(cityName: String) {
        lastCityUriAdd = database.addCity(cityName)
        displayCity = true
        loaderManager.restartLoader(CITY_LOADER, arguments, this)

    }

    private fun displayCurrentCity() {
        if (lastCityUriAdd != null) {
            val city = database.getCity(lastCityUriAdd!!)
            if (city != null) {
                val position = recyclerViewAdapter.positionOfCity(city)
                onCitySelected(lastCityUriAdd!!, position)
            }
        }
    }
}
