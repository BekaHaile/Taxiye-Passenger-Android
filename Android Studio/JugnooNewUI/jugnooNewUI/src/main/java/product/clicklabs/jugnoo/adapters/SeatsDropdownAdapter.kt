package product.clicklabs.jugnoo.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.retrofit.model.Gender
import product.clicklabs.jugnoo.utils.Fonts

class SeatsDropdownAdapter(context: Context, resource: Int, objects: MutableList<Int>) : ArrayAdapter<Int>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getView(position, convertView, parent)
        if (view is TextView) {
            view.typeface = Fonts.mavenMedium(context)
            view.setTextColor(ContextCompat.getColor(parent.context, R.color.text_color))
        }

        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = super.getDropDownView(position, convertView, parent)
        if (view is TextView) {
            view.typeface = Fonts.mavenMedium(context)
            view.setTextColor(ContextCompat.getColor(parent!!.context, R.color.text_color))
        }

        return view
    }

}