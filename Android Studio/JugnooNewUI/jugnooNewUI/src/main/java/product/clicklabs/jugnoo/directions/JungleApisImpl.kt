package product.clicklabs.jugnoo.directions

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.datastructure.MapsApiSources
import product.clicklabs.jugnoo.directions.room.database.DirectionsPathDatabase
import product.clicklabs.jugnoo.directions.room.model.Path
import product.clicklabs.jugnoo.directions.room.model.Point
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.PlaceDetailsResponse
import product.clicklabs.jugnoo.retrofit.model.PlaceDetailsResponseGoogle
import product.clicklabs.jugnoo.retrofit.model.PlacesAutocompleteResponse
import product.clicklabs.jugnoo.utils.GoogleRestApis
import product.clicklabs.jugnoo.utils.Log
import product.clicklabs.jugnoo.utils.MapUtils
import product.clicklabs.jugnoo.utils.Prefs
import retrofit.mime.TypedByteArray
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

object JungleApisImpl {

    private val gson = Gson()

    private const val JUNGLE_TYPE_VALUE = "android"
    private const val JUNGLE_OFFERING_VALUE = "18"

    private var db: DirectionsPathDatabase? = null
        get() {
            if (field == null) {
                field = DirectionsPathDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    private var numberFormat: NumberFormat? = null
        get() {
            if(field == null){
                field = NumberFormat.getInstance(Locale.ENGLISH)
                field!!.minimumFractionDigits = 4
                field!!.maximumFractionDigits = 4
                field!!.roundingMode = RoundingMode.HALF_UP
                field!!.isGroupingUsed = false
            }
            return field
        }

    fun getDirectionsPath(source:LatLng, destination:LatLng, units:String, apiSource:String, callback:Callback?) {

        GlobalScope.launch(Dispatchers.IO){
            try {
                val directionsResult = getDirectionsPathSync(source, destination, units, apiSource, true)
                if(directionsResult != null){
                    launch(Dispatchers.Main){callback?.onSuccess(directionsResult.latLngs, directionsResult.path)}
                } else {
                    launch(Dispatchers.Main){callback?.onFailure()}
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main){callback?.onFailure()}
            }
        }

    }

    fun putJungleOptionsParams(params:HashMap<String, String>, jungleObj:JSONObject){
        val option = jungleObj.optInt(Constants.KEY_JUNGLE_OPTIONS, 0)
        params[Constants.KEY_JUNGLE_FM_TOKEN] = Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_FM_API_KEY_ANDROID_CUSTOMER, "")
        params[Constants.KEY_JUNGLE_OPTIONS] = option.toString()
        params[Constants.KEY_JUNGLE_TYPE] = JUNGLE_TYPE_VALUE
        params[Constants.KEY_JUNGLE_OFFERING] = JUNGLE_OFFERING_VALUE

        when(option){
            1 -> { //here map
                params[Constants.KEY_JUNGLE_APP_ID] = jungleObj.optString(Constants.KEY_JUNGLE_APP_ID)
                params[Constants.KEY_JUNGLE_APP_CODE] = jungleObj.optString(Constants.KEY_JUNGLE_APP_CODE)
            }
            2 -> { //google
                params[Constants.KEY_JUNGLE_API_KEY] = jungleObj.optString(Constants.KEY_JUNGLE_API_KEY)
            }
            3 -> { //map box
                params[Constants.KEY_JUNGLE_ACCESS_TOKEN] = jungleObj.optString(Constants.KEY_JUNGLE_ACCESS_TOKEN)
            }
        }
    }

    fun getDirectionsPathSync(source:LatLng, destination:LatLng, units:String, apiSource:String, fallbackNeeded:Boolean = true) : DirectionsResult? {
        var directionsResult:DirectionsResult? = null

        //safety check to not hit directions api if distance between source and destination is more than 200Kms(server configurable)
        try {
            val directionsDistanceThreshold = Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_DIRECTIONS_MAX_DISTANCE_THRESHOLD, "200000.0")
            if(MapUtils.distance(source, destination) > directionsDistanceThreshold.toDouble()){
                Log.e(JungleApisImpl::class.java.simpleName+"_distance_threshold_case", "directionsDistanceThreshold=$directionsDistanceThreshold, distance="+MapUtils.distance(source, destination))
                return directionsResult
            }
        } catch (e: Exception) {}

        val timeStamp = System.currentTimeMillis()

        val sourceLat = numberFormat!!.format(source.latitude).toDouble()
        val sourceLng = numberFormat!!.format(source.longitude).toDouble()
        val destinationLat = numberFormat!!.format(destination.latitude).toDouble()
        val destinationLng = numberFormat!!.format(destination.longitude).toDouble()

        val paths = db!!.getDao().getPath(
                sourceLat,
                sourceLng,
                destinationLat,
                destinationLng,
                timeStamp - Constants.DAY_MILLIS*30)

        val cachingEnabled = Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_DIRECTIONS_CACHING, 1) == 1
        //path is not found
        if(!cachingEnabled || paths == null || paths.isEmpty()){

            //separate configuration for directions api in case of fare estimate
            var objKey = Constants.KEY_JUNGLE_DIRECTIONS_OBJ
            if(apiSource.equals(MapsApiSources.CUSTOMER_FARE_ESTIMATE_HOME, true)
                    || apiSource.equals(MapsApiSources.CUSTOMER_FARE_ESTIMATE_SCHEDULE, true)
                    || apiSource.equals(MapsApiSources.CUSTOMER_FARE_ESTIMATE_ACTIVITY, true)){
                objKey = Constants.KEY_CFE_JUNGLE_DIRECTIONS_OBJ
            }

            val jungleObj = try{JSONObject(Prefs.with(MyApplication.getInstance()).getString(objKey, Constants.EMPTY_JSON_OBJECT))}
                            catch(e:java.lang.Exception){JSONObject(Constants.EMPTY_JSON_OBJECT)}
            Log.e(JungleApisImpl::class.java.simpleName, "jungleObj=$jungleObj, objKey=$objKey")


            try {
                if(checkIfJungleApiEnabled(jungleObj)){

                    val pointsJ = JSONArray()
                    val startJ = JSONObject()
                    startJ.put(Constants.KEY_LAT, sourceLat.toString()).put(Constants.KEY_LNG, sourceLng.toString())
                    val destJ = JSONObject()
                    destJ.put(Constants.KEY_LAT, destinationLat.toString()).put(Constants.KEY_LNG, destinationLng.toString())
                    pointsJ.put(startJ).put(destJ)

                    val params = HashMap<String, String>()
                    params[Constants.KEY_JUNGLE_POINTS] = pointsJ.toString()

                    putJungleOptionsParams(params, jungleObj)

                    val response = RestClient.getJungleMapsApi().directions(params)


                    val result = String((response.body as TypedByteArray).bytes)
                    val jObj = JSONObject(result)

                    val list = mutableListOf<LatLng>()
                    list.addAll(MapUtils.getLatLngListFromPathJungle(result))
                    val distanceValue = jObj.getJSONObject("data").getJSONArray("paths").getJSONObject(0).getDouble("distance")
                    val timeValue = jObj.getJSONObject("data").getJSONArray("paths").getJSONObject(0).getDouble("time")/1000

                    val path = Path(
                            sourceLat,
                            sourceLng,
                            destinationLat,
                            destinationLng,
                            distanceValue, timeValue,
                            timeStamp)

                    directionsResult = DirectionsResult(list, path)

                } else {
                    throw Exception()
                }

            } catch (e: Exception) {
                //if exception encountered in jungle directions api and fallback to google is not needed then return error case
                if(checkIfJungleApiEnabled(jungleObj) && !fallbackNeeded){
                    Log.e(JungleApisImpl::class.java.simpleName, "_fallback_not_needed_case")
                    return directionsResult
                }

                //google directions hit
                try {
                    val response = GoogleRestApis.getDirections("$sourceLat,$sourceLng", "$destinationLat,$destinationLng",
                            false, "driving", false, units, apiSource)
                    val result = String((response.body as TypedByteArray).bytes)
                    val jObj = JSONObject(result)

                    val list = mutableListOf<LatLng>()
                    list.addAll(MapUtils.getLatLngListFromPath(result))
                    val distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value")
                    val timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value")

                    val path = Path(
                            sourceLat,
                            sourceLng,
                            destinationLat,
                            destinationLng,
                            distanceValue, timeValue,
                            timeStamp)

                    directionsResult = DirectionsResult(list, path)
                } catch (e: Exception) {
                }
            }


            if(cachingEnabled && directionsResult != null) {
                db!!.getDao().deleteAllPath(timeStamp)
                db!!.getDao().insertPath(directionsResult.path)

                //inserting path points
                val points = mutableListOf<Point>()
                for (latlng in directionsResult.latLngs) {
                    points.add(Point(timeStamp, latlng.latitude, latlng.longitude))
                }
                db!!.getDao().insertPathPoints(points)
            }

        } else {
            val segments = db!!.getDao().getPathPoints(paths[0].timeStamp)
            if (segments != null) {
                val list = mutableListOf<LatLng>()
                for(segment in segments){
                    list.add(LatLng(segment.lat, segment.lng))
                }
                directionsResult = DirectionsResult(list, paths[0])
            }
        }
        return directionsResult
    }


    fun getDistanceMatrix(sourceLatLng: LatLng, destLatLng: LatLng) : DistanceMatrixResult? {
        var distanceMatrixResult:DistanceMatrixResult? = null
        try {
            val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_DISTANCE_MATRIX_OBJ, Constants.EMPTY_JSON_OBJECT))
            if(checkIfJungleApiEnabled(jungleObj)){

                val params = HashMap<String, String>()
                params[Constants.KEY_JUNGLE_ORIGIN_LAT] = sourceLatLng.latitude.toString()
                params[Constants.KEY_JUNGLE_ORIGIN_LNG] = sourceLatLng.longitude.toString()
                params[Constants.KEY_JUNGLE_DEST_LAT] = destLatLng.latitude.toString()
                params[Constants.KEY_JUNGLE_DEST_LNG] = destLatLng.longitude.toString()

                putJungleOptionsParams(params, jungleObj)

                val response = RestClient.getJungleMapsApi().distancematrix(params)

                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val distStr = jObj.getJSONObject("data").getString("distance")
                val timeStr = jObj.getJSONObject("data").getString("Time")

                val distance = if(jObj.getJSONObject("data").has("distance_in_meter")){
                    jObj.getJSONObject("data").getDouble("distance_in_meter")
                } else if(distStr.contains(' ')){
                    distStr.split(' ')[0].toDouble() * 1000.0
                } else {
                    distStr.toDouble()
                }

                val time = if(jObj.getJSONObject("data").has("time_in_second")){
                    jObj.getJSONObject("data").getDouble("time_in_second")
                } else if(timeStr.contains(' ')){
                    timeStr.split(' ')[0].toDouble()
                } else {
                    timeStr.toDouble()
                }

                distanceMatrixResult = DistanceMatrixResult(distance, time)
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            try {//google distance matrix hit
                val response = GoogleRestApis.getDistanceMatrix("${sourceLatLng.latitude},${sourceLatLng.longitude}", "${destLatLng.latitude},${destLatLng.longitude}",
                        "EN", false, false)
                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val distanceValue = jObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getDouble("value")
                val timeValue = jObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getDouble("value")
                distanceMatrixResult = DistanceMatrixResult(distanceValue, timeValue)
            } catch (e: Exception) {
            }
        }
        return distanceMatrixResult
    }

    fun getGeocodeAddress(sourceLatLng: LatLng, language:String) : GeocodeResult? {
        var geocodeResult:GeocodeResult? = null
        try {
            val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_GEOCODE_OBJ, Constants.EMPTY_JSON_OBJECT))
            if(checkIfJungleApiEnabled(jungleObj)){

                val params = HashMap<String, String>()
                params[Constants.KEY_JUNGLE_LAT] = sourceLatLng.latitude.toString()
                params[Constants.KEY_JUNGLE_LNG] = sourceLatLng.longitude.toString()

                putJungleOptionsParams(params, jungleObj)

                val response = RestClient.getJungleMapsApi().searchReverse(params)

                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val address = jObj.getJSONObject("data").getString("address")

                geocodeResult = GeocodeResult(null, address, true)
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            try {//google reverse geocode hit
                val response = GoogleRestApis.geocode("${sourceLatLng.latitude},${sourceLatLng.longitude}", language)
                val result = String((response!!.body as TypedByteArray).bytes)
                val googleGeocodeResponse = gson.fromJson(result, GoogleGeocodeResponse::class.java)
                if (googleGeocodeResponse.results != null && googleGeocodeResponse.results!!.isNotEmpty()) {
                    geocodeResult = GeocodeResult(googleGeocodeResponse, null, false)
                }
            } catch (e: Exception) {
            }
        }
        return geocodeResult
    }

    fun getAutoCompletePredictions(input:String, sessiontoken:String, components:String, location: String, radius:String) : AutoCompleteResult? {
        var autoCompleteResult:AutoCompleteResult? = null
        try {
            val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_AUTOCOMPLETE_OBJ, Constants.EMPTY_JSON_OBJECT))
            if(checkIfJungleApiEnabled(jungleObj)){

                val arr = location.split(",")
                val params = HashMap<String, String>()
                params[Constants.KEY_JUNGLE_CURRENT_LAT] = arr[0]
                params[Constants.KEY_JUNGLE_CURRENT_LNG] = arr[1]
                params[Constants.KEY_JUNGLE_TEXT] = input

                putJungleOptionsParams(params, jungleObj)

//                if(!params.containsKey(Constants.KEY_JUNGLE_API_KEY)){
//                    params[Constants.KEY_JUNGLE_API_KEY] = GoogleRestApis.MAPS_BROWSER_KEY()
//                }

                val response = RestClient.getJungleMapsApi().search(params)

                val result = String((response.body as TypedByteArray).bytes)
                val placesAutocompleteResponse = gson.fromJson(result, PlacesAutocompleteResponse::class.java)
                if (placesAutocompleteResponse.predictions != null && placesAutocompleteResponse.predictions!!.isNotEmpty()) {
                    autoCompleteResult = AutoCompleteResult(placesAutocompleteResponse, true)
                } else {
                    throw Exception()
                }
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            try {//google auto-complete hit
                val response = GoogleRestApis.getAutoCompletePredictions(input, sessiontoken, components, location, radius)
                val result = String((response.body as TypedByteArray).bytes)
                val placesAutocompleteResponse = gson.fromJson(result, PlacesAutocompleteResponse::class.java)
                if (placesAutocompleteResponse.predictions != null && placesAutocompleteResponse.predictions!!.isNotEmpty()) {
                    autoCompleteResult = AutoCompleteResult(placesAutocompleteResponse, false)
                }
            } catch (e: Exception) {
            }
        }
        return autoCompleteResult
    }

    fun getPlaceById(placeId:String, latLng:LatLng, sessiontoken:String) : PlaceDetailResult? {
        var placeDetailResult:PlaceDetailResult? = null
        try {
            val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_AUTOCOMPLETE_OBJ, Constants.EMPTY_JSON_OBJECT))
            if(checkIfJungleApiEnabled(jungleObj)){

                val params = HashMap<String, String>()
                params[Constants.KEY_JUNGLE_CURRENT_LAT] = latLng.latitude.toString()
                params[Constants.KEY_JUNGLE_CURRENT_LNG] = latLng.longitude.toString()
                params[Constants.KEY_JUNGLE_PLACEID] = placeId

                params[Constants.KEY_JUNGLE_API_KEY] = if(jungleObj.has(Constants.KEY_JUNGLE_API_KEY)){
                    jungleObj.optString(Constants.KEY_JUNGLE_API_KEY)
                } else {
                    GoogleRestApis.MAPS_BROWSER_KEY()
                }
                params[Constants.KEY_JUNGLE_FM_TOKEN] = Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_FM_API_KEY_ANDROID_CUSTOMER, "")

                params[Constants.KEY_JUNGLE_TYPE] = JUNGLE_TYPE_VALUE
                params[Constants.KEY_JUNGLE_OFFERING] = JUNGLE_OFFERING_VALUE

                val response = RestClient.getJungleMapsApi().geocodePlaceById(params)

                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val placeDetailsResponse = gson.fromJson(jObj.getJSONObject("data").toString(), PlaceDetailsResponse::class.java)
                if (placeDetailsResponse.results != null && placeDetailsResponse.results!!.isNotEmpty()) {
                    placeDetailResult = PlaceDetailResult(placeDetailsResponse, true)
                } else {
                    throw Exception()
                }
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            try {//google auto-complete hit
                val response = GoogleRestApis.getPlaceDetails(placeId, sessiontoken)
                val result = String((response.body as TypedByteArray).bytes)
                val placeDetailsResponseGoogle = gson.fromJson(result, PlaceDetailsResponseGoogle::class.java)
                if (placeDetailsResponseGoogle.result != null) {

                    val placeDetailResponse = PlaceDetailsResponse()
                    placeDetailResponse.results = mutableListOf()
                    placeDetailResponse.results!!.add(placeDetailsResponseGoogle.result!!)

                    placeDetailResult = PlaceDetailResult(placeDetailResponse, false)
                }
            } catch (e: Exception) {
            }
        }
        return placeDetailResult
    }


    fun checkIfJungleApiEnabled(jungleObj:JSONObject) : Boolean{
        return jungleObj.optInt(Constants.KEY_JUNGLE_OPTIONS, -1) != -1
    }

    fun deleteDirectionsPathOld(){
        GlobalScope.launch(Dispatchers.IO){
            db!!.getDao().deleteOldPaths(System.currentTimeMillis() - Constants.DAY_MILLIS*30)
        }
    }

    interface Callback{
        fun onSuccess(latLngs:MutableList<LatLng>, path:Path)
        fun onFailure()
    }

    data class DirectionsResult(val latLngs:MutableList<LatLng>, val path:Path)
    data class DistanceMatrixResult(val distanceValue:Double, val timeValue:Double)
    data class GeocodeResult(val googleGeocodeResponse: GoogleGeocodeResponse?, val singleAddress:String?, val junglePassed:Boolean)
    data class AutoCompleteResult(val placesAutocompleteResponse: PlacesAutocompleteResponse, val junglePassed:Boolean)
    data class PlaceDetailResult(val placeDetailsResponse: PlaceDetailsResponse, val junglePassed:Boolean)

}