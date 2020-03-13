package com.sabkuchfresh.retrofit.model.feed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images implements Parcelable {

    @SerializedName("tab_normal")
    @Expose
    private String tabNormal;

    @SerializedName("tab_highlighted")
    @Expose
    private String tabHighlighted;

    @SerializedName("ride_now_normal")
    @Expose
    private String rideNowNormal;

    @SerializedName("ride_now_highlighted")
    @Expose
    private String rideNowHighlighted;

    @SerializedName("driver_icon")
    @Expose
    private String driverIcon;

    @SerializedName("marker_icon")
    @Expose
    private String markerIcon;

    public String getTabNormal() {
        return tabNormal;
    }

    public String getTabHighlighted() {
        return tabHighlighted;
    }

    public String getRideNowNormal() {
        return rideNowNormal;
    }

    public String getRideNowHighlighted() {
        return rideNowHighlighted;
    }

    public String getDriverIcon() {
        return driverIcon;
    }

    public String getMarkerIcon() {
        return markerIcon;
    }

    protected Images(Parcel in) {
        tabNormal = in.readString();
        tabHighlighted = in.readString();
        rideNowNormal = in.readString();
        rideNowHighlighted = in.readString();
        driverIcon = in.readString();
        markerIcon=in.readString();
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tabNormal);
        parcel.writeString(tabHighlighted);
        parcel.writeString(rideNowNormal);
        parcel.writeString(rideNowHighlighted);
        parcel.writeString(driverIcon);
        parcel.writeString(markerIcon);
    }
}
