package product.clicklabs.jugnoo.home.schedulerides

import android.content.Context
import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.widget.ImageView
import product.clicklabs.jugnoo.R

/**
 * Created by Parminder Saini on 11/10/18.
 */


object BindingAdapters{


    @BindingAdapter("app:noDataIcon")
    @JvmStatic  fun noDataIcon(view: ImageView,state: DataState) {

        view.setImageDrawable(getImageForDataState(state,view.context))
    }


    fun getImageForDataState(state: DataState,context: Context):Drawable?{
        val image:Int =  when(state){
            DataState.NO_INTERNET -> R.drawable.no_internet_icon
            DataState.EMPTY_DATA -> R.drawable.ic_no_items
            DataState.LOADING -> R.drawable.ic_vehicle_loader

        }
        return ContextCompat.getDrawable(context,image)
    }



}