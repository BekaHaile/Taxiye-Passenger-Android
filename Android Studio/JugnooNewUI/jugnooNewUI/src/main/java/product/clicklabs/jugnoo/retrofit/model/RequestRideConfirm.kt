package product.clicklabs.jugnoo.retrofit.model

import android.os.Parcel
import android.os.Parcelable

data class RequestRideConfirm(var pickup : String?,
                              var drop : String?,
                              var vehicleIcon : String?,
                              var vehicleName : String?,
                              var note : String?,
                              var estimateFare : String?,
                              var fare : Double = 0.0,
                              var minFare : Double = 0.0,
                              var maxFare : Double = 0.0,
                              var currency : String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pickup)
        parcel.writeString(drop)
        parcel.writeString(vehicleIcon)
        parcel.writeString(vehicleName)
        parcel.writeString(note)
        parcel.writeString(estimateFare)
        parcel.writeDouble(fare)
        parcel.writeDouble(minFare)
        parcel.writeDouble(maxFare)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RequestRideConfirm> {
        override fun createFromParcel(parcel: Parcel): RequestRideConfirm {
            return RequestRideConfirm(parcel)
        }

        override fun newArray(size: Int): Array<RequestRideConfirm?> {
            return arrayOfNulls(size)
        }
    }
}