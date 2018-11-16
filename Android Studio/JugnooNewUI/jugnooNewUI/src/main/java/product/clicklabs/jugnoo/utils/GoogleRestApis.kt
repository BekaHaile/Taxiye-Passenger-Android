package product.clicklabs.jugnoo.utils

import android.util.Base64
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse
import product.clicklabs.jugnoo.AccessTokenGenerator
import product.clicklabs.jugnoo.BuildConfig
import product.clicklabs.jugnoo.Constants
import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.home.HomeUtil
import product.clicklabs.jugnoo.retrofit.RestClient
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object GoogleRestApis {

    private fun MAPS_CLIENT(): String {
        return Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_MAPS_API_CLIENT, BuildConfig.MAPS_CLIENT)
    }

    private fun MAPS_BROWSER_KEY(): String {
        return Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_MAPS_API_BROWSER_KEY, BuildConfig.MAPS_BROWSER_KEY)
    }

    private fun MAPS_APIS_SIGN(): Boolean {
        return Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_MAPS_API_SIGN, if (BuildConfig.MAPS_APIS_SIGN) 1 else 0) == 1
    }

    private fun MAPS_PRIVATE_KEY(): String {
        return Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_MAPS_API_PRIVATE_KEY, BuildConfig.MAPS_PRIVATE_KEY)
    }

    /**
     * for driver animation, and path animation
     */
    fun getDirections(originLatLng: String, destLatLng: String, sensor: Boolean?,
                      mode: String, alternatives: Boolean?, units: String): Response {
        val response:Response
        if (MAPS_APIS_SIGN()) {
            val urlToSign = ("/maps/api/directions/json?" +
                    "origin=" + originLatLng
                    + "&destination=" + destLatLng
                    + "&sensor=" + sensor
                    + "&mode=" + mode
                    + "&alternatives=" + alternatives
                    + "&units=" + units
                    + "&client=" + MAPS_CLIENT())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            response = RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
                    sensor, mode, alternatives, units, MAPS_CLIENT(), googleSignature)
        } else {
            response = RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
                    sensor, mode, alternatives, units, MAPS_BROWSER_KEY())
        }
        if(originLatLng.contains(",")) {
            logGoogleRestAPI(originLatLng.split(",")[0], originLatLng.split(",")[1], API_NAME_DIRECTIONS)
        }
        return response
    }

    /**
     * for fare estimate
     */
    fun getDirections(originLatLng: String, destLatLng: String, sensor: Boolean?,
                      mode: String, alternatives: Boolean?, units: String, callback: Callback<SettleUserDebt>) {
        if (MAPS_APIS_SIGN()) {
            val urlToSign = ("/maps/api/directions/json?" +
                    "origin=" + originLatLng
                    + "&destination=" + destLatLng
                    + "&sensor=" + sensor
                    + "&mode=" + mode
                    + "&alternatives=" + alternatives
                    + "&units=" + units
                    + "&client=" + MAPS_CLIENT())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
                    sensor, mode, alternatives, units, MAPS_CLIENT(), googleSignature, callback)
        } else {
            RestClient.getGoogleApiService().getDirections(originLatLng, destLatLng,
                    sensor, mode, alternatives, units, MAPS_BROWSER_KEY(), callback)
        }
        if(originLatLng.contains(",")) {
            logGoogleRestAPIC(originLatLng.split(",")[0], originLatLng.split(",")[1], API_NAME_DIRECTIONS)
        }
    }

    fun getDistanceMatrix(originLatLng: String, destLatLng: String, language: String,
                          sensor: Boolean?, alternatives: Boolean?): Response {
        val response:Response
        if (MAPS_APIS_SIGN()) {
            val urlToSign = ("/maps/api/distancematrix/json?" +
                    "origins=" + originLatLng
                    + "&destinations=" + destLatLng
                    + "&language=" + language
                    + "&sensor=" + sensor
                    + "&alternatives=" + alternatives
                    + "&client=" + MAPS_CLIENT())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            response = RestClient.getGoogleApiService().getDistanceMatrix(originLatLng, destLatLng, language,
                    sensor, alternatives, MAPS_CLIENT(), googleSignature)
        } else {
            response = RestClient.getGoogleApiService().getDistanceMatrix(originLatLng, destLatLng, language,
                    sensor, alternatives, MAPS_BROWSER_KEY())
        }
        if(originLatLng.contains(",")) {
            logGoogleRestAPI(originLatLng.split(",")[0], originLatLng.split(",")[1], API_NAME_DISTANCE_MATRIX)
        }
        return response
    }

    fun geocode(latLng: String, language: String): Response {
        Log.i("GoogleRestApi", "geocode")
        val response:Response
        if (MAPS_APIS_SIGN()) {
            val urlToSign = ("/maps/api/geocode/json?" +
                    "latlng=" + latLng
                    + "&language=" + language
                    + "&sensor=" + false
                    + "&client=" + MAPS_CLIENT())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            response = RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_CLIENT(), googleSignature)
        } else {
            response = RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_BROWSER_KEY())
        }
        if(latLng.contains(",")) {
            logGoogleRestAPI(latLng.split(",")[0], latLng.split(",")[1], API_NAME_GEOCODE)
        }
        return response
    }

    fun geocode(latLng: String, language: String, callback: Callback<GoogleGeocodeResponse>) {
        if (checkApiLimit(callback)) return

        Log.i("GoogleRestApi", "geocode")
        if (MAPS_APIS_SIGN()) {
            val urlToSign = ("/maps/api/geocode/json?" +
                    "latlng=" + latLng
                    + "&language=" + language
                    + "&sensor=" + false
                    + "&client=" + MAPS_CLIENT())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_CLIENT(), googleSignature, callback)
        } else {
            RestClient.getGoogleApiService().geocode(latLng, language, false, MAPS_BROWSER_KEY(), callback)
        }
        if(latLng.contains(",")) {
            logGoogleRestAPIC(latLng.split(",")[0], latLng.split(",")[1], API_NAME_GEOCODE)
        }
    }

    private fun checkApiLimit(callback: Callback<GoogleGeocodeResponse>): Boolean {
        if(Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_GEOCODE_LIMIT_ENABLED, 0) == 0){
            return false
        }
        val firstTime = Prefs.with(MyApplication.getInstance()).getLong(Constants.SP_FIRST_GEOCODE_TIMESTAMP, 0L)
        var apiCount = Prefs.with(MyApplication.getInstance()).getInt(Constants.SP_GEOCODE_HITS_COUNT, 0)
        val timeLimitMillis = Prefs.with(MyApplication.getInstance()).getLong(Constants.KEY_CUSTOMER_GEOCODE_TIME_LIMIT, Constants.DAY_MILLIS)
        val apiCountLimit = Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_GEOCODE_HIT_LIMIT, 30)

        val currentTime = System.currentTimeMillis()
        val timeDIff = currentTime - firstTime

        if (firstTime > 0 && timeDIff < timeLimitMillis && apiCount >= apiCountLimit) {
            val t = GoogleGeocodeResponse()
            t.status = "DENIED"
            t.results = arrayListOf()
            callback.success(t, null)
            return true
        } else if (firstTime == 0L || timeDIff >= timeLimitMillis) {
            Prefs.with(MyApplication.getInstance()).save(Constants.SP_FIRST_GEOCODE_TIMESTAMP, currentTime)
            apiCount = 1
        } else if (apiCount < apiCountLimit) {
            apiCount++
        }
        Prefs.with(MyApplication.getInstance()).save(Constants.SP_GEOCODE_HITS_COUNT, apiCount)
        return false
    }


    fun getDirectionsWaypoints(strOrigin: String, strDestination: String, strWaypoints: String): Response {
        val response:Response
        if (MAPS_APIS_SIGN()) {
            val urlToSign = ("/maps/api/directions/json?" +
                    "origin=" + strOrigin
                    + "&destination=" + strDestination
                    + "&waypoints=" + strWaypoints
                    + "&client=" + MAPS_CLIENT())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }


            response = RestClient.getGoogleApiService().getDirectionsWaypoints(strOrigin, strDestination,
                    strWaypoints, MAPS_CLIENT(), googleSignature)
        } else {
            response = RestClient.getGoogleApiService().getDirectionsWaypoints(strOrigin, strDestination,
                    strWaypoints, MAPS_BROWSER_KEY())
        }
        if(strOrigin.contains(",")) {
            logGoogleRestAPI(strOrigin.split(",")[0], strOrigin.split(",")[1], API_NAME_DIRECTIONS)
        }
        return response
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun generateGoogleSignature(urlToSign: String): String {

        // Convert the key from 'web safe' base 64 to binary
        var keyString = MAPS_PRIVATE_KEY()
        keyString = keyString.replace('-', '+')
        keyString = keyString.replace('_', '/')
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        val key = Base64.decode(keyString, Base64.DEFAULT)


        val sha1Key = SecretKeySpec(key, "HmacSHA1")

        // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(sha1Key)

        // compute the binary signature for the request
        val sigBytes = mac.doFinal(urlToSign.toByteArray())

        // base 64 encode the binary signature
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        var signature = Base64.encodeToString(sigBytes, Base64.DEFAULT)

        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-')
        signature = signature.replace('/', '_')

        return signature
    }


    fun logGoogleRestAPI(latitude: String, longitude: String, apiName: String) {
        if(Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_GOOGLE_APIS_LOGGING, 0) == 1) {
            val map = hashMapOf<String, String>()
            map[Constants.KEY_ACCESS_TOKEN] = AccessTokenGenerator.getAccessTokenPair(MyApplication.getInstance()).first
            map[Constants.KEY_LATITUDE] = latitude
            map[Constants.KEY_LONGITUDE] = longitude
            map[Constants.KEY_API_NAME] = apiName
            HomeUtil.addDefaultParams(map)
            RestClient.getApiService().logGoogleApiHits(map)
        }
    }
    fun logGoogleRestAPIC(latitude: String, longitude: String, apiName: String) {
        if(Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CUSTOMER_GOOGLE_APIS_LOGGING, 0) == 1) {
            val map = hashMapOf<String, String>()
            map[Constants.KEY_ACCESS_TOKEN] = AccessTokenGenerator.getAccessTokenPair(MyApplication.getInstance()).first
            map[Constants.KEY_LATITUDE] = latitude
            map[Constants.KEY_LONGITUDE] = longitude
            map[Constants.KEY_API_NAME] = apiName
            HomeUtil.addDefaultParams(map)
            RestClient.getApiService().logGoogleApiHitsC(map, object : Callback<SettleUserDebt> {
                override fun success(t: SettleUserDebt?, response: Response?) {
                }

                override fun failure(error: RetrofitError?) {
                }
            })
        }
    }


    const val API_NAME_DIRECTIONS = "directions"
    const val API_NAME_DISTANCE_MATRIX = "distance_matrix"
    const val API_NAME_GEOCODE = "geocode"
    const val API_NAME_PLACES = "places"
    const val API_NAME_AUTOCOMPLETE = "autocomplete"

}
