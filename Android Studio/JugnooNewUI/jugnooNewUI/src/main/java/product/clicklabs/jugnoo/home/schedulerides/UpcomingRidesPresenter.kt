package product.clicklabs.jugnoo.home.schedulerides

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.library.baseAdapters.BR
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName

/**
 * Created by Parminder Saini on 11/10/18.
 */

class UpcomingRidesPresenter(var view:UpcomingRidesContract.View,
                             var apiCommon: ApiCommon<UpcomingRideResponse>)
             :UpcomingRidesContract.Presenter,BaseObservable() {


    var listScheduleRideData:List<UpcomingRide>?=null

    var dataState =  ObservableField<DataState>(DataState.EMPTY_DATA)


    init {

        view.setPresenter(this)
    }



    override fun start() {
        getAllUpcomingRides()
    }

    override fun getAllUpcomingRides() {

        apiCommon.putAccessToken(true).execute(hashMapOf(),ApiName.GET_UPCOMING_RIDES
            ,object: APICommonCallback<UpcomingRideResponse>(){
            override fun onSuccess(t: UpcomingRideResponse, message: String?, flag: Int) {
                with(t.list){
                    listScheduleRideData = this
                    view.showAllUpcomingRides(this)

                }


            }

            override fun onError(t: UpcomingRideResponse?, message: String?, flag: Int): Boolean {
                listScheduleRideData = null
                return false
            }

            override fun onFailure(error: Exception): Boolean {
                listScheduleRideData = null
                return super.onFailure(error)
            }

            override fun onNotConnected(): Boolean {
                listScheduleRideData = null
                return super.onNotConnected()
            }

            override fun onFinish() {
                notifyPropertyChanged(BR.upcomingRidesExist)

            }
        })
    }


    @Bindable
     fun isUpcomingRidesExist():Boolean{
        return listScheduleRideData?.run {
            size > 0
        } ?: false
    }


    fun deleteScheduleRide(upcomingRide: UpcomingRide){
       view.deleteScheduleRide(upcomingRide)

    }


}

enum class DataState {
    LOADING,
    NO_INTERNET,
    EMPTY_DATA
}
