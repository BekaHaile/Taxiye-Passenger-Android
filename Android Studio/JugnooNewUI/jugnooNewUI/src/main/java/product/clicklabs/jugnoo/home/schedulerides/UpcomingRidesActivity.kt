package product.clicklabs.jugnoo.home.schedulerides

import android.databinding.DataBindingUtil
import android.os.Bundle
import product.clicklabs.jugnoo.BaseAppCompatActivity
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.databinding.ActivityUpcomingRidesBinding

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val upcomingRidesBinding:ActivityUpcomingRidesBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_upcoming_rides)
        adapter = UpcomingRidesAdapter(this)
        upcomingRidesBinding.rvUpcomingRides.adapter = adapter

        UpcomingRidesPresenter(this).start()

    }



}