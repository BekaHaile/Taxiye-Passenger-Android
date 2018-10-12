package product.clicklabs.jugnoo.home.schedulerides

import product.clicklabs.jugnoo.home.BasePresenter
import product.clicklabs.jugnoo.home.BaseView

/**
 * Created by Parminder Saini on 11/10/18.
 */
interface UpcomingRidesContract {


    interface Presenter : BasePresenter {

        fun getAllUpcomingRides()

    }

    interface View : BaseView<Presenter> {

        fun showAllUpcomingRides()


    }

}