package product.clicklabs.jugnoo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PlaceDetailsResponse {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("result")
    @Expose
    var result: Result? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

}
class Result {

    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

}
class Geometry {

    @SerializedName("location")
    @Expose
    var location: Location? = null

}
class Location {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null

}