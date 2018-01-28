package com.meteo.iut.meteo.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.meteo.iut.meteo.R
import com.meteo.iut.meteo.utils.Extra

class DeleteCityDialogFragment : DialogFragment() {

    interface DeleteCityDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    companion object {
        fun newInstance(cityName: String) : DeleteCityDialogFragment {
            val fragment = DeleteCityDialogFragment()
            fragment.arguments = Bundle().apply {
                putString(Extra.EXTRA_CITY_NAME, cityName)
            }

            return fragment
        }
    }

    var listener: DeleteCityDialogListener? = null
    private lateinit var cityName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cityName = arguments!!.getString(Extra.EXTRA_CITY_NAME)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.deletecity_title, cityName))
                .setPositiveButton(getString(R.string.commun_yes), { _, _ -> listener?.onDialogPositiveClick()} )
                .setNegativeButton(getString(R.string.commun_no), { _, _ -> listener?.onDialogNegativeClick()} )

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}