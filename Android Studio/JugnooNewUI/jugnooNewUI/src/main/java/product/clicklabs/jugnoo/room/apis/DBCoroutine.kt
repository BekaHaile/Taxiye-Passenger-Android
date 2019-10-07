package product.clicklabs.jugnoo.room.apis

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import product.clicklabs.jugnoo.Data
import product.clicklabs.jugnoo.room.SearchLocation
import product.clicklabs.jugnoo.room.database.SearchLocationDB
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.MapUtils
import product.clicklabs.jugnoo.utils.Utils

class DBCoroutine {

    companion object {
        fun insertLocation(searchLocationDB: SearchLocationDB, searchLocation: SearchLocation) {
            GlobalScope.launch(Dispatchers.IO) {
                val search: List<SearchLocation>
                if (searchLocation.type == 0) {
                    search = searchLocationDB.getSearchLocation().getPickupLocations()
                } else {
                    search = searchLocationDB.getSearchLocation().getDropLocations()
                }

                if(checkForAddLocation(search, searchLocation) == -1) {
                    searchLocationDB.getSearchLocation().insertSearchedLocation(searchLocation)
                }
            }
        }

        fun checkForAddLocation(search: List<SearchLocation>, searchLocation: SearchLocation) : Int {
            var selectedNearByAddress : SearchLocation? = null
            for (i in 0 until search.size) {
                var distance = java.lang.Double.MAX_VALUE
                val compareDistance : Double = if(Data.autoData.useRecentLocAutoSnapMaxDistance !=null) Data.autoData.useRecentLocAutoSnapMaxDistance else 0.0
                val fetchedDistance = MapUtils.distance(LatLng(searchLocation.slat, searchLocation.sLng), LatLng(search[i].slat, search[i].slat))
//                if (fetchedDistance <= compareDistance && fetchedDistance < distance) {
//                    distance = fetchedDistance
//                    selectedNearByAddress = search[i]
//                }

                if (searchLocation.slat == search[i].slat && searchLocation.sLng == search[i].sLng) {
                    selectedNearByAddress = search[i]
                }
            }
            if(selectedNearByAddress == null || Utils.compareDouble(selectedNearByAddress.slat, 0.0) == 0 || Utils.compareDouble(selectedNearByAddress.sLng, 0.0) == 0) {
                return -1
            } else
                return selectedNearByAddress.id
        }

        fun deleteLocation(searchLocationDB: SearchLocationDB, searchLocation: SearchLocation) {
            GlobalScope.launch(Dispatchers.IO) {
                val search = searchLocationDB.getSearchLocation().getLocation()
                val id = checkForAddLocation(search, searchLocation)
                if(id != -1) {
                    searchLocationDB.getSearchLocation().deleteLocation(id)
                }
            }
        }

        fun deleteLocationIfDatePassed(searchLocationDB: SearchLocationDB) {
            GlobalScope.launch(Dispatchers.IO) {
                val search = searchLocationDB.getSearchLocation().getLocation()
                for (i in 0 until search.size) {
                    val time = DateOperations.getTimeDifference(DateOperations.getTimeStampUTCFromMillis(search[i].date, false), DateOperations.getDaysAheadTime(DateOperations.getCurrentTime(), 15))
                    if(time > 0) {
                        deleteLocation(searchLocationDB, search[i])
                    }
                }
            }
        }

        fun getPickupLocation(searchLocationDB: SearchLocationDB, searchLocationCallback: SearchLocationCallback) {
            GlobalScope.launch(Dispatchers.Main) {
                val searchLocation : List<SearchLocation> = withContext(Dispatchers.IO) {
                   searchLocationDB.getSearchLocation().getPickupLocations()
                }
                searchLocationCallback.onSearchLocationReceived(searchLocation)
            }
        }
        fun getDropLocation(searchLocationDB: SearchLocationDB, searchLocationCallback: SearchLocationCallback) {
            GlobalScope.launch(Dispatchers.Main) {
                val searchLocation : List<SearchLocation> = withContext(Dispatchers.IO) {
                   searchLocationDB.getSearchLocation().getDropLocations()
                }
                searchLocationCallback.onSearchLocationReceived(searchLocation)
            }
        }
        fun getAllLocations(searchLocationDB: SearchLocationDB, searchLocationCallback: SearchLocationCallback) {
            GlobalScope.launch(Dispatchers.Main) {
                val searchLocation : List<SearchLocation> = withContext(Dispatchers.IO) {
                   searchLocationDB.getSearchLocation().getLocation()
                }
                searchLocationCallback.onSearchLocationReceived(searchLocation)
            }
        }

    }

    interface SearchLocationCallback{
        fun onSearchLocationReceived(searchLocation : List<SearchLocation>)
    }
}
