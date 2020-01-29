package product.clicklabs.jugnoo

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.sabkuchfresh.feed.ui.api.APICommonCallback
import com.sabkuchfresh.feed.ui.api.ApiCommon
import com.sabkuchfresh.feed.ui.api.ApiName
import kotlinx.android.synthetic.main.activity_fare_details.*
import kotlinx.android.synthetic.main.layout_appbar.*
import product.clicklabs.jugnoo.adapters.FareDetailsAdapter
import product.clicklabs.jugnoo.adapters.FareExtra
import product.clicklabs.jugnoo.adapters.FareItem
import product.clicklabs.jugnoo.adapters.FareVehicle
import product.clicklabs.jugnoo.retrofit.model.FareDetailsResponse
import product.clicklabs.jugnoo.utils.DialogPopup
import product.clicklabs.jugnoo.utils.Utils

class FareDetailsActivity : BaseAppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fare_details)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_selector)
        }
        tvTitle.text = getString(R.string.fare_details)

        rvFareDetails.layoutManager = LinearLayoutManager(this)


        val latitude = intent.getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude)
        val longitude = intent.getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude)
        apiFareDetails(latitude, longitude)
    }

    override fun attachBaseContext(newBase: Context?) {
        attachBaseContextWithoutTypekit(newBase)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun apiFareDetails(latitude:Double, longitude:Double){
        val params :HashMap<String, String> = HashMap()
        params[Constants.KEY_LATITUDE] = latitude.toString()
        params[Constants.KEY_LONGITUDE] = longitude.toString()
        ApiCommon<FareDetailsResponse>(this).execute(params, ApiName.FARE_DETAILS, object : APICommonCallback<FareDetailsResponse>(){
            override fun onSuccess(t: FareDetailsResponse?, message: String?, flag: Int) {
                val details: ArrayList<Any> = ArrayList()
                t?.regions?.forEach {
                    details.add(FareVehicle(it.regionName+"("+it.maxPeople+")"))
                    it.fare?.let {
                        if(it.fareFixed > 0) {
                            details.add(FareItem(getString(R.string.base_fare), Utils.formatCurrencyValue(t.currency, it.fareFixed, false)))
                        }
                        if(it.farePerMin > 0) {
                            details.add(FareItem(getString(R.string.nl_per_min), Utils.formatCurrencyValue(t.currency, it.farePerMin, false)))
                        }
                        if(it.farePerWaitingMin > 0) {
                            details.add(FareItem(getString(R.string.per_wait_min), Utils.formatCurrencyValue(t.currency, it.farePerWaitingMin, false)))
                        }
                        if(it.farePerKm > 0) {
                            details.add(FareItem(getString(R.string.per_format, Utils.getDistanceUnit(t.distanceUnit)),
                                    Utils.formatCurrencyValue(t.currency, it.farePerKm, false)))
                        }
                    }

                }
                if(t != null && t.rateCardInfo.isNotEmpty()){
                    details.add(FareExtra(t.rateCardInfo))
                }

                val adapter = FareDetailsAdapter(details)
                rvFareDetails.adapter = adapter
            }

            override fun onError(t: FareDetailsResponse?, message: String?, flag: Int): Boolean {
                DialogPopup.alertPopupWithListener(this@FareDetailsActivity, "", message) { onBackPressed() }
                return true
            }

        })
    }

}