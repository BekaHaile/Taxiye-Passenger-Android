package product.clicklabs.jugnoo.home.schedulerides

import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import com.sabkuchfresh.feed.models.FeedCommonResponse
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import product.clicklabs.jugnoo.BaseAppCompatActivity
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.databinding.ActivityUpcomingRidesBinding
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Fonts

/**
 * Created by Parminder Saini on 10/10/18.
 */

class UpcomingRidesActivity:BaseAppCompatActivity(),UpcomingRidesContract.View{


    lateinit  var mPresenter:UpcomingRidesContract.Presenter
    lateinit var adapter: UpcomingRidesAdapter

    override fun setPresenter(presenter: UpcomingRidesContract.Presenter) {
        this.mPresenter = presenter
    }


    override fun showAllUpcomingRides(list:List<UpcomingRide>) {
        adapter.submitList(list)
    }

    override fun deleteScheduleRide(upcomingRide: UpcomingRide) {
        DialogPopup.alertPopupTwoButtonsWithListeners(this,getString(R.string.are_you_sure_delete_schedule_ride)) {

            ApiCommon<FeedCommonResponse>(this).putAccessToken(true).
                    execute(hashMapOf("pickup_id" to upcomingRide.engagementId),
                    ApiName.DELETE_SCHEDULE_RIDE, object : APICommonCallback<FeedCommonResponse>(){
                override fun onSuccess(t: FeedCommonResponse?, message: String?, flag: Int) {
                            mPresenter.getAllUpcomingRides()
                }

                override fun onError(t: FeedCommonResponse?, message: String?, flag: Int): Boolean {
                    return false
                }

            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var upcompingRidePresenter = UpcomingRidesPresenter(this, ApiCommon(this))
        upcompingRidePresenter.start()

        val upcomingRidesBinding:ActivityUpcomingRidesBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_upcoming_rides)
       upcomingRidesBinding.presenter = upcompingRidePresenter
        upcomingRidesBinding.textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD)
        adapter = UpcomingRidesAdapter(this,upcompingRidePresenter)
        upcomingRidesBinding.rvUpcomingRides.adapter = adapter


    }



}