package product.clicklabs.jugnoo.home

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.R
import product.clicklabs.jugnoo.datastructure.MapsApiSources
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode
import product.clicklabs.jugnoo.directions.JungleApisImpl
import product.clicklabs.jugnoo.utils.ASSL
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.Prefs

object DriverToPickupPath{

    private var TAG:String = DriverToPickupPath::class.java.simpleName

    private const val DRIVER_TO_PICKUP_PATH_ZINDEX = 0f

    private var polylineDriverToPickup: Polyline? = null



    fun showPath(context: Context, passengerScreenMode: PassengerScreenMode, map: GoogleMap,
                 driverLatLng: LatLng, pickupLatLng: LatLng){
        Log.d(TAG, "showPath  passengerScreenMode= $passengerScreenMode, $map")
        if(Prefs.with(context).getInt(Constants.KEY_DRIVER_TO_PICKUP_PATH_ENABLED, 1) == 1
                && (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode
                || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)) {
            GlobalScope.launch(Dispatchers.IO) {
                val directionsResult = JungleApisImpl.getDirectionsPathSync(driverLatLng, pickupLatLng, "metric", MapsApiSources.CUSTOMER_DRIVER_TO_PICKUP, false)
                showPolylineDriverToPickup(context, directionsResult, passengerScreenMode, map)
            }
        }

    }

    private fun showPolylineDriverToPickup(context: Context, directionsResult: JungleApisImpl.DirectionsResult?,
                                           passengerScreenMode: PassengerScreenMode, map: GoogleMap) {
        GlobalScope.launch(Dispatchers.Main){
            Log.d(TAG, "showPolylineDriverToPickup passengerScreenMode= $passengerScreenMode, directionsResult= $directionsResult, $map")
            if (directionsResult?.latLngs != null
                    && (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode || PassengerScreenMode.P_DRIVER_ARRIVED == passengerScreenMode)) {
                if (polylineDriverToPickup != null) {
                    Log.d(TAG, "showPolylineDriverToPickup polylineDriverToPickup removed")
                    polylineDriverToPickup!!.remove()
                }
                val polylineOptions = PolylineOptions()
                polylineOptions.width(ASSL.Xscale() * 5f)
                        .color(ContextCompat.getColor(context, R.color.google_path_polyline_color))
                        .geodesic(true).zIndex(DRIVER_TO_PICKUP_PATH_ZINDEX)
                polylineOptions.addAll(directionsResult.latLngs)
                Log.d(TAG, "showPolylineDriverToPickup ploylline shown")
                polylineDriverToPickup = map.addPolyline(polylineOptions)
            }
        }
    }


    fun removePolylineDriverToPickup(passengerScreenMode: PassengerScreenMode) {
        GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG, "removePolylineDriverToPickup passengerScreenMode= $passengerScreenMode")
            if (PassengerScreenMode.P_REQUEST_FINAL != passengerScreenMode
                    && PassengerScreenMode.P_DRIVER_ARRIVED != passengerScreenMode
                    && polylineDriverToPickup != null) {
                polylineDriverToPickup!!.remove()
            }
        }
    }

}