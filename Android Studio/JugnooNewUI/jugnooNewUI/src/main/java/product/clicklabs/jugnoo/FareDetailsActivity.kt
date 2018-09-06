package product.clicklabs.jugnoo

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_fare_details.*
import kotlinx.android.synthetic.main.layout_appbar.*
import product.clicklabs.jugnoo.R.id.rvFareDetails
import product.clicklabs.jugnoo.adapters.FareDetailsAdapter
import product.clicklabs.jugnoo.adapters.FareExtra
import product.clicklabs.jugnoo.adapters.FareItem
import product.clicklabs.jugnoo.adapters.FareVehicle
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

        val details: ArrayList<Any> = ArrayList()
        Data.autoData.regions.forEach {
            details.add(FareVehicle(it.regionName+"("+it.maxPeople+")"))
            details.add(FareItem(getString(R.string.base_fare)+":", it.fareStructure.getDisplayBaseFare(this).trim()))
            details.add(FareItem(getString(R.string.nl_per_min)+":", Utils.formatCurrencyValue(Data.autoData.fareStructure.currency, it.fareStructure.farePerMin)))
            details.add(FareItem(getString(R.string.per_format, Utils.getDistanceUnit(Data.autoData.fareStructure.distanceUnit))+":",
                    Utils.formatCurrencyValue(Data.autoData.fareStructure.currency, it.fareStructure.farePerKm)))
            details.add(FareExtra(Data.autoData.fareStructure.getDisplayFareText(this)))
        }

        val adapter = FareDetailsAdapter(details)
        rvFareDetails.adapter = adapter

    }

    override fun attachBaseContext(newBase: Context?) {
        attachBaseContextWithoutTypekit(newBase)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}