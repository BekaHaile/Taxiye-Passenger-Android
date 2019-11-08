package product.clicklabs.jugnoo.room.apis

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import product.clicklabs.jugnoo.room.DBObject
import product.clicklabs.jugnoo.room.database.SearchLocationDB
import product.clicklabs.jugnoo.room.model.SearchLocation
import product.clicklabs.jugnoo.utils.DateOperations
import product.clicklabs.jugnoo.utils.MapUtils
import product.clicklabs.jugnoo.utils.Utils

class DBCoroutine {

    companion object {
        var exceptionCount : Int = 0
        fun insertLocation(searchLocationDB: SearchLocationDB, searchLocation: SearchLocation) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val search: List<SearchLocation> = if (searchLocation.type == 0) {
                        searchLocationDB.getSearchLocation().getLocation()   // get all location rather than only pickup(display logic changed)
                    } else {
                        searchLocationDB.getSearchLocation().getLocation()   // get all location rather than only drop(display logic changed)
                    }

                    if(checkForAddLocation(search, searchLocation) == -1) {
                        searchLocationDB.getSearchLocation().insertSearchedLocation(searchLocation)
                    }
                } catch (e: Exception) {
                    if(exceptionCount == 0) {
                        exceptionCount += 1
                        SearchLocationDB.clearInstance()
                        DBObject.clearInstance()
                        val searchDB = DBObject.getInstance()
                        if(searchDB != null) {
                            insertLocation(searchDB, searchLocation)
                        }
                    } else {
                        exceptionCount = 0
                    }
                }
            }
        }

        private fun checkForAddLocation(search: List<SearchLocation>, searchLocation: SearchLocation) : Int {
            var selectedNearByAddress : SearchLocation? = null
            for (i in search.indices) {
                val compareDistance = 50.0
                val fetchedDistance = MapUtils.distance(LatLng(searchLocation.slat, searchLocation.sLng), LatLng(search[i].slat, search[i].sLng))
                if (fetchedDistance <= compareDistance) {
                    selectedNearByAddress = search[i]
                }

//                if (searchLocation.slat == search[i].slat && searchLocation.sLng == search[i].sLng) {
//                    selectedNearByAddress = search[i]
//                }
            }
            return if(selectedNearByAddress == null || Utils.compareDouble(selectedNearByAddress.slat, 0.0) == 0 || Utils.compareDouble(selectedNearByAddress.sLng, 0.0) == 0) {
                -1
            } else
                selectedNearByAddress.id
        }

        fun deleteLocation(searchLocationDB: SearchLocationDB, searchLocation: SearchLocation) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val search = searchLocationDB.getSearchLocation().getLocation()
                    val id = checkForAddLocation(search, searchLocation)
                    if(id != -1) {
                        searchLocationDB.getSearchLocation().deleteLocation(id)
                    }
                } catch (e: Exception) {
                }
            }
        }

        fun deleteLocationIfDatePassed(searchLocationDB: SearchLocationDB) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val search = searchLocationDB.getSearchLocation().getLocation()
                    for (i in search.indices) {
                        val time = DateOperations.getTimeDifference(DateOperations.getTimeStampUTCFromMillis(search[i].date, false), DateOperations.getDaysAheadTime(DateOperations.getCurrentTime(), 15))
                        if(time > 0) {
                            deleteLocation(searchLocationDB, search[i])
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }

        fun deleteAll(searchLocationDB: SearchLocationDB) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    searchLocationDB.getSearchLocation().deleteAll()
                } catch (e: Exception) {
                }
            }
        }

        fun getPickupLocation(searchLocationDB: SearchLocationDB, searchLocationCallback: SearchLocationCallback) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val searchLocation : List<SearchLocation> = withContext(Dispatchers.IO) {
                       searchLocationDB.getSearchLocation().getPickupLocations()
                    }
                    searchLocationCallback.onSearchLocationReceived(searchLocation)
                } catch (e: Exception) {
                }
            }
        }
        fun getDropLocation(searchLocationDB: SearchLocationDB, searchLocationCallback: SearchLocationCallback) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val searchLocation : List<SearchLocation> = withContext(Dispatchers.IO) {
                       searchLocationDB.getSearchLocation().getDropLocations()
                    }
                    searchLocationCallback.onSearchLocationReceived(searchLocation)
                } catch (e: Exception) {
                }
            }
        }
        fun getAllLocations(searchLocationDB: SearchLocationDB, searchLocationCallback: SearchLocationCallback) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val searchLocation : List<SearchLocation> = withContext(Dispatchers.IO) {
                       searchLocationDB.getSearchLocation().getLocation()
                    }
                    searchLocationCallback.onSearchLocationReceived(searchLocation)
                } catch (e: Exception) {
                }
            }
        }

    }

    interface SearchLocationCallback{
        fun onSearchLocationReceived(searchLocation : List<SearchLocation>)
    }
}
