package product.clicklabs.jugnoo.home.schedulerides

import android.databinding.Bindable
import android.databinding.ObservableField
import product.clicklabs.jugnoo.home.schedulerides.BindingAdapters.DataState.*

/**
 * Created by Parminder Saini on 11/10/18.
 */

class UpcomingRidesPresenter:UpcomingRidesContract.Presenter{


    var listScheduleRideData:List<ScheduleRideData>?=null

    val dataState =  ObservableField<BindingAdapters.DataState>(LOADING)


    override fun start() {

    }

    override fun getAllUpcomingRides() {


    }


    @Bindable
    fun isUpcomingRidesExist():Boolean{
        return listScheduleRideData?.size==0
    }





}