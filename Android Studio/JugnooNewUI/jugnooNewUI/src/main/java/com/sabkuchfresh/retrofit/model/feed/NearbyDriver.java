package com.sabkuchfresh.retrofit.model.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearbyDriver implements Parcelable {

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("distance_with_units")
    @Expose
    private String distance_with_units;

    @SerializedName("vehicle_type")
    @Expose
    private int vehicleType;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("phone_no")
    @Expose
    private String phoneNo;

    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;

    @SerializedName("distance")
    @Expose
    private double distance;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDistance_with_units() {
        return distance_with_units;
    }

    public int getVehicle_type() {
        return vehicleType;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.distance_with_units);
        dest.writeInt(this.vehicleType);
        dest.writeString(this.userName);
        dest.writeString(this.phoneNo);
        dest.writeString(this.vehicleName);
        dest.writeDouble(this.distance);
    }

    public NearbyDriver() {
    }

    protected NearbyDriver(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distance_with_units = in.readString();
        this.vehicleType = in.readInt();
    }

    public static final Parcelable.Creator<NearbyDriver> CREATOR = new Parcelable.Creator<NearbyDriver>() {
        @Override
        public NearbyDriver createFromParcel(Parcel source) {
            return new NearbyDriver(source);
        }

        @Override
        public NearbyDriver[] newArray(int size) {
            return new NearbyDriver[size];
        }
    };
}
