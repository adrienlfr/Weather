package com.meteo.iut.meteo.utils

import android.content.Context
import android.media.MediaRouter
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.meteo.iut.meteo.adapter.CityRecyclerViewAdapter
import com.meteo.iut.meteo.adapter.RecyclerViewCursorAdapter

/**
 * Created by jigon on 04/02/2018.
 */
class DragManageAdapter(adapter: CityRecyclerViewAdapter, context: Context, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs)
{
    var theAdapter = adapter

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
    {
        theAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int)
    {
    }

}