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
import com.meteo.iut.meteo.utils.SwipeToDeleteCallback
import com.meteo.iut.meteo.utils.toast


class CityFragment : Fragment(), CityRecyclerViewAdapter.CityItemListener, LoaderManager.LoaderCallbacks<Cursor> {

    interface CityFragmentListener {
        fun onCitySelected(uriCity: Uri)
        fun onEmptyCities()
    }

    var listener: CityFragmentListener? = null
    private val CITY_LOADER = 0

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
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCitySelected(uriCity: Uri) {
        listener?.onCitySelected(uriCity)
    }

    override fun onCityDeleted(cursor: Cursor) {
        showDeleteCityDialog(cursor)
    }

    private fun selectFirstCity(){
        when(recyclerViewAdapter.itemCount > 0) {
            true -> onCitySelected(recyclerViewAdapter.getItem(0)!!.notificationUri)
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
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

        deleteCityFragment.show(fragmentManager, "DeleteCityDialogFragment")
    }

    private fun deleteCity(cityName: String) {
        if ( database.deleteCity(cityName) ) {
            context.toast(getString(R.string.deletecity_found, cityName))
            if ((activity as CityActivity).isTwoPane && (activity as CityActivity).currentUriCity != null) {
                if (database.getCity((activity as CityActivity).currentUriCity!!) == null)
                    selectFirstCity()
            }
        }else{
            context.toast(getString(R.string.deletecity_impossible, cityName))
        }
    }

    private fun saveCity(cityName: String) {
        val uriCity = database.addCity(cityName)
        onCitySelected(Uri.parse("${CityContract.BASE_CONTENT_URI}/$uriCity"))
    }
}
