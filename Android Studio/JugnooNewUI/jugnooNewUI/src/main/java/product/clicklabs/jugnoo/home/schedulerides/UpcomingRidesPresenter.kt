package product.clicklabs.jugnoo.home.schedulerides

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.ObservableField

/**
 * Created by Parminder Saini on 11/10/18.
 */

class UpcomingRidesPresenter(var view:UpcomingRidesContract.View):UpcomingRidesContract.Presenter,BaseObservable() {


    var listScheduleRideData:List<UpcomingRide>?=null

    var dataState =  ObservableField<DataState>(DataState.LOADING)

    var upcomingRidesExist =  ObservableField(false)

    init {

        view.setPresenter(this)
    }



    override fun start() {
        getAllUpcomingRides()
    }

    override fun getAllUpcomingRides() {


    }


    @Bindable
     fun isUpcomingRidesExist():Boolean{
        return listScheduleRideData?.size==0
    }



}

enum class DataState {
    LOADING,
    NO_INTERNET,
    EMPTY_DATA

}
