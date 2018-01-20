package com.meteo.iut.meteo.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import com.meteo.iut.meteo.App
import com.meteo.iut.meteo.Database
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.data.City
import com.meteo.iut.meteo.adapter.CityAdapter
import com.meteo.iut.meteo.dialog.CreateCityDialogFragment
import com.meteo.iut.meteo.dialog.DeleteCityDialogFragment
import com.meteo.iut.meteo.utils.SwipeToDeleteCallback
import com.meteo.iut.meteo.utils.toast


/**
 * Created by adrien on 10/01/2018.
 */
class CityFragment : Fragment(), CityAdapter.CityItemListener {

    interface CityFragmentListener {
        fun onCitySelected(city: City)
    }

    var listener: CityFragmentListener? = null

    private lateinit var villes: MutableList<City>
    private lateinit var database : Database
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = App.database
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_city, container, false)
        recyclerView = view.findViewById(R.id.cities_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(this.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val city = adapter.getItem(viewHolder.adapterPosition)
                showDeleteCityDialog(city)
            }
        }

        val touchHelper = ItemTouchHelper(swipeHandler)
        touchHelper.attachToRecyclerView(recyclerView)

        floatingButton = view.findViewById(R.id.floatingActionButton)
        floatingButton.setOnClickListener({v -> showCreateCityDialog()})
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        villes = database.getAllCities()
        adapter = CityAdapter(villes, this)
        recyclerView.adapter = adapter
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

    override fun onCitySelected(city: City) {
        listener?.onCitySelected(city)
    }

    override fun onCityDeleted(city: City) {
        showDeleteCityDialog(city)
    }

    private fun showCreateCityDialog() {
        val createCityFragment = CreateCityDialogFragment()
        createCityFragment.listener = object : CreateCityDialogFragment.CreateCityDialogListerner {
            override fun onDialogPositiveClick(cityName: String) {
                saveCity(City(cityName))
            }

            override fun onDialogNegativeClick() { }
        }

        createCityFragment.show(fragmentManager, "CreateCityDialogFragment")
    }

    private fun showDeleteCityDialog(city: City) {
        val deleteCityFragment = DeleteCityDialogFragment.newInstance(city.name)
        deleteCityFragment.listener = object: DeleteCityDialogFragment.DeleteCityDialogListener {
            override fun onDialogPositiveClick() {
                deleteCity(city)
            }

            override fun onDialogNegativeClick() {
                adapter.notifyDataSetChanged()
            }
        }


        deleteCityFragment.show(fragmentManager, "DeleteCityDialogFragment")
    }

    private fun deleteCity(city: City) {
        if ( database.deleteCity(city) ) {
            villes.remove(city)
            adapter.notifyDataSetChanged()
            context.toast(getString(R.string.deletecity_found, city.name))
        }else{
            context.toast(getString(R.string.deletecity_impossible, city.name))
        }
    }

    private fun saveCity(city: City) {
        if (database.createCity(city)) {
            villes.add(city)
            adapter.notifyDataSetChanged()
        }else{
            context.toast(getString(R.string.createcity_impossible))
        }
    }
}
