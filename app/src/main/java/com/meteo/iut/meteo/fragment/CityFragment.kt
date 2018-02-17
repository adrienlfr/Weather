package com.meteo.iut.meteo.fragment

import android.database.Cursor
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
import android.widget.Toast

import com.meteo.iut.meteo.App
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.activity.CityActivity
import com.meteo.iut.meteo.adapter.CityRecyclerViewAdapter
import com.meteo.iut.meteo.database.CityContract
import com.meteo.iut.meteo.database.CityContract.CityEntry
import com.meteo.iut.meteo.database.CityCursorWrapper
import com.meteo.iut.meteo.database.CityQuery
import com.meteo.iut.meteo.dialog.CreateCityDialogFragment
import com.meteo.iut.meteo.utils.SwipeToDeleteCallback
import com.meteo.iut.meteo.utils.toast


class CityFragment : Fragment(), CityRecyclerViewAdapter.CityItemListener, LoaderManager.LoaderCallbacks<Cursor> {

    interface CityFragmentListener {
        fun onCitySelected(uriCity: Uri, position: Int?)
        fun onEmptyCities()
        fun onClickNewNotification()
    }

    private var emptyView: View? = null

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
        emptyView = view?.findViewById(R.id.empty_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val glm = GridLayoutManager(recyclerView.context, 1)
        recyclerView.layoutManager = glm

        recyclerViewAdapter = CityRecyclerViewAdapter(this)
        recyclerView.adapter = recyclerViewAdapter

        checkShowEmptyView()


        val swipeHandler = object : SwipeToDeleteCallback(this.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val cursor = recyclerViewAdapter.getItem(viewHolder.adapterPosition)
                cursor.let {onCityDeleted(cursor!!)}
            }
        }

        val touchHelper = ItemTouchHelper(swipeHandler)
        touchHelper.attachToRecyclerView(recyclerView)

        floatingButton = view.findViewById(R.id.floatingActionButton)
        floatingButton.setOnClickListener({_ -> showCreateCityDialog()})
        return view
    }

    private fun checkShowEmptyView() {
        if (recyclerViewAdapter.itemCount > 0) {
            emptyView!!.setVisibility(View.GONE)
        } else {
            emptyView!!.setVisibility(View.VISIBLE)
        }
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loaderManager.initLoader(CITY_LOADER, null, this)
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
        checkShowEmptyView()
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        recyclerViewAdapter.swapCursor(data)
        if (displayCity){
            displayCurrentCity()
            displayCity = false
        }
        checkShowEmptyView()
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
        val values = CityCursorWrapper(cursor).getCityContentValues()
        val cityName = values.getAsString(CityEntry.CITY_KEY_NAME)
        deleteCity(cityName)
    }

    private fun selectFirstCity(){
        when(recyclerViewAdapter.itemCount > 0) {
            true -> onCitySelected(recyclerViewAdapter.getItem(0)!!.notificationUri, null)
            false -> listener?.onEmptyCities()
        }
    }


    private fun showCreateCityDialog() {
        val createCityFragment = CreateCityDialogFragment()
        createCityFragment.listener = object : CreateCityDialogFragment.CreateCityDialogListener {
            override fun onDialogPositiveClick(cityName: String) {
                if (cityName.isNotEmpty()){
                    saveCity(cityName.capitalize())
                } else {
                    context.toast(getString(R.string.city_name_empty), Toast.LENGTH_LONG)
                    showCreateCityDialog()
                }
            }

            override fun onDialogNegativeClick() { }
        }

        createCityFragment.show(fragmentManager, "CreateCityDialogFragment")
    }

    private fun deleteCity(cityName: String) {
        if ( database.deleteCity(cityName) ) {
            context.toast(getString(R.string.deletecity_found, cityName))

            if ((activity as CityActivity).isTwoPane) {
                CityActivity.currentUriCity?.let {
                    if (database.getCity(it) == null)
                        selectFirstCity()
                }
            }
        }else{
            context.toast(getString(R.string.deletecity_impossible, cityName))
        }
        checkShowEmptyView()
    }

    private fun saveCity(cityName: String) {
        lastCityUriAdd = database.addCity(cityName)
        displayCity = true
        loaderManager.restartLoader(CITY_LOADER, arguments, this)
        checkShowEmptyView()
    }

    private fun displayCurrentCity() {
        lastCityUriAdd?.let { lastCityUriAdd ->
            database.getCity(lastCityUriAdd)?.let { city ->
                val position = recyclerViewAdapter.positionOfCity(city)
                onCitySelected(lastCityUriAdd, position)
            }
        }
    }
}
