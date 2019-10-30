package product.clicklabs.jugnoo.directions

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.directions.room.database.DirectionsPathDatabase
import product.clicklabs.jugnoo.directions.room.model.Path
import product.clicklabs.jugnoo.directions.room.model.Point
import product.clicklabs.jugnoo.utils.GoogleRestApis
import product.clicklabs.jugnoo.utils.MapUtils
import retrofit.mime.TypedByteArray
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

object GAPIDirections {

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
                field!!.minimumFractionDigits = 3
                field!!.maximumFractionDigits = 3
                field!!.roundingMode = RoundingMode.HALF_UP
                field!!.isGroupingUsed = false
            }
            return field
        }

    fun getDirectionsPath(source:LatLng, destination:LatLng, units:String, apiSource:String, callback:Callback?) {

        GlobalScope.launch(Dispatchers.IO){
            try {
                val directionsResult = getDirectionsPathSync(source, destination, units, apiSource)
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

    fun getDirectionsPathSync(source:LatLng, destination:LatLng, units:String, apiSource:String) : DirectionsResult? {
        var directionsResult:DirectionsResult? = null
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

        //path is not found
        if(paths == null || paths.isEmpty()){
            val list = mutableListOf<LatLng>()

            //google directions hit
            val response = GoogleRestApis.getDirections("$sourceLat,$sourceLng", "$destinationLat,$destinationLng",
                    false, "driving", false, units, apiSource)
            val result = String((response.body as TypedByteArray).bytes)
            val jObj = JSONObject(result)

            list.addAll(MapUtils.getLatLngListFromPath(result))

            val startAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address")
            val endAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address")

            val distanceText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text")
            val timeText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text")

            val distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value")
            val timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value")

            //inserting path
            val path = Path(
                    sourceLat,
                    sourceLng,
                    destinationLat,
                    destinationLng,
                    distanceValue, timeValue,
                    startAddress, endAddress, distanceText, timeText,
                    timeStamp)

            db!!.getDao().deleteAllPath(timeStamp)
            db!!.getDao().insertPath(path)

            //inserting path points
            val points = mutableListOf<Point>()
            for(latlng in list){
                points.add(Point(timeStamp, latlng.latitude, latlng.longitude))
            }
            db!!.getDao().insertPathPoints(points)

            directionsResult = DirectionsResult(list, path)
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

}